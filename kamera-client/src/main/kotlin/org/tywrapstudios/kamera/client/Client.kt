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
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.kamera.api.LinkService
import java.util.Scanner
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun main() = runBlocking {
    val scanner = Scanner(System.`in`)
    while (isActive) {
        try {
            val ktorClient = createClient()
            val client: KtorRpcClient = ktorClient.createRpc()

            while (isActive) {
                try {
                    val nextLine = scanner.nextLine()
                    if (nextLine.startsWith(">>ch ")) {
                        client.withService<ChatService>()
                            .sendMessage("Test", nextLine.replace(">>ch ", ""))
                    } else if (nextLine.startsWith(">>link ")) {
                        println(nextLine.replace(">>link ", ""))
                        println(nextLine.replace(">>link ", "").split(":"))
                        println(nextLine.replace(">>link ", "").split(":").first())
                        val link = client.withService<LinkService>()
                            .generateCode(Uuid.parseHex(nextLine.replace(">>link ", "").split(":").first()), nextLine.replace(">>link ", "").split(":").last().toULong())
                        println("${link?.code}, ${link?.expires}")
                    } else if (nextLine.startsWith(">>checklink ")) {
                        val response = client.withService<LinkService>()
                            .getLinkStatus(Uuid.parseHex(nextLine.replace(">>checklink ", "")))
                        println("${response?.uuid?.toHexDashString()} : ${response?.snowflake}, ${response?.expires}, ${response?.verified}")
                    } else if (nextLine.startsWith(">>unlink ")) {
                        client.withService<LinkService>()
                            .unlink(Uuid.parseHex(nextLine.replace(">>unlink ", "")))
                    } else {
                        println(client.withService<CommandService>()
                            .run(nextLine))
                    }
                } catch (e: IllegalStateException) {
                    println("Error: $e")
                    if (e.message?.contains("cancelled") == true) {
                        break
                    }
                } catch (e: Throwable) {
                    println("Error: $e")
                    e.printStackTrace()
                }
            }

            ktorClient.close()
            client.close()
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