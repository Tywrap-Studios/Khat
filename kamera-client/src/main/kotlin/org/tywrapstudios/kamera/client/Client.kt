package org.tywrapstudios.kamera.client

import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.CommandService

fun main() = runBlocking {
    val ktorClient = HttpClient {
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
    command.run("kill @a")
}