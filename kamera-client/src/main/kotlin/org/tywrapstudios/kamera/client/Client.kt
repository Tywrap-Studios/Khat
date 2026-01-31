package org.tywrapstudios.kamera.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.http.encodedPath
import kotlinx.coroutines.isActive
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
    val scanner = Scanner(System.`in`)
    while (isActive) {
        try {
            val ktorClient = createClient()
            val client: KtorRpcClient = ktorClient.createRpc()
            val command: CommandService = client.withService<CommandService>()

            while (isActive) {
                try {
                    println(command.run(scanner.nextLine()))
                } catch (e: IllegalStateException) {
                    println("Error: $e")
                    if (e.message?.contains("cancelled") == true) {
                        break
                    }
                } catch (e: Throwable) {
                    println("Error: $e")
                }
            }

            ktorClient.close()
        } catch (e: Throwable) {
            println("Error: $e")
        }
    }
}

fun createClient() = HttpClient {
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

fun HttpClient.createRpc() = this.rpc {
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