package org.tywrapstudios.krapher

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.http.encodedPath
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.HealthService

object KameraClient {
    private lateinit var ktorClient: HttpClient
    private fun createKtor() = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(BotInitializer.config.mRpcToken, null)
                }
            }
        }

        installKrpc {
            serialization { json() }
        }
    }

    private lateinit var rpcClient: KtorRpcClient
    private fun HttpClient.createRpc() = this.rpc {
        url {
            host = "127.0.0.1"
            port = BotInitializer.config.mRpcPort
            encodedPath = "kamera"
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    fun connect() {
        ktorClient = createKtor()
        rpcClient = ktorClient.createRpc()
    }

    suspend fun getClient(): KtorRpcClient {
        try {
            logger.info("Checking health of client.")
            rpcClient.withService<HealthService>().isOnline()
            return rpcClient
        } catch (e: IllegalStateException) {
            if (e.message?.contains("cancelled") == true) {
                logger.info("Client was cancelled, reconnecting...")
                return reconnect()
            }
        } catch (e: Throwable) {
            throw e
        }
        return rpcClient
    }

    private fun reconnect(): KtorRpcClient {
        ktorClient.close()
        ktorClient = createKtor()
        rpcClient.close()
        rpcClient = ktorClient.createRpc()
        return rpcClient
    }
}