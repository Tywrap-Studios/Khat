package org.tywrapstudios.krapher

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.http.*
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ServerStatsService

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

    suspend fun get(): KtorRpcClient {
        try {
            logger.debug("Checking health of client.")
            val online = rpcClient.withService<ServerStatsService>().isOnline()
            val players = rpcClient.withService<ServerStatsService>().playerCount()
            val max = rpcClient.withService<ServerStatsService>().maximumPlayers()
            logger.debug("Client is operable. Server returned status: ${if (online) "n" else "f"}${players}x${max}.")
            return rpcClient
        } catch (e: IllegalStateException) {
            if (e.message?.contains("cancelled") == true) {
                logger.warn("Client was cancelled, reconnecting...")
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