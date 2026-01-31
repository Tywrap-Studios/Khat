package org.tywrapstudios.kamera.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.http.encodedPath
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.CommandService
import java.util.Scanner

fun main() = runBlocking {
    val ktorClient = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens("Testing101", null)
                }
            }
        }

        installKrpc {
            serialization { json() }
        }
    }

    val client: KtorRpcClient = ktorClient.rpc {
        url {
            host = "127.0.0.1"
            port = 34230
            encodedPath = "kamera"
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    val command: CommandService = client.withService<CommandService>()
    while (true) try {
        val scanner = Scanner(System.`in`)
        command.run(scanner.nextLine())
    } catch (e: Throwable) {
        println(e.message)
    }
}