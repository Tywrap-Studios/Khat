package org.tywrapstudios.kamera.client

import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.kamera.api.LinkService
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun main() = runBlocking {
    val scanner = Scanner(System.`in`)
    KameraClient.connect()
    while (isActive) {
        try {
            val nextLine = scanner.nextLine()
            if (nextLine.startsWith(">>ch ")) {
                KameraClient.get().withService<ChatService>()
                    .sendMessage("Test", nextLine.replace(">>ch ", ""))
            } else if (nextLine.startsWith(">>link ")) {
                println(nextLine.replace(">>link ", ""))
                println(nextLine.replace(">>link ", "").split(":"))
                println(nextLine.replace(">>link ", "").split(":").first())
                val link = KameraClient.get().withService<LinkService>()
                    .generateCode(
                        Uuid.parseHex(nextLine.replace(">>link ", "").split(":").first()),
                        nextLine.replace(">>link ", "").split(":").last().toULong()
                    )
                println("${link?.code}, ${link?.expires}")
            } else if (nextLine.startsWith(">>checklink ")) {
                val response = KameraClient.get().withService<LinkService>()
                    .getLinkStatus(Uuid.parseHex(nextLine.replace(">>checklink ", "")))
                println("${response?.uuid?.toHexDashString()} : ${response?.snowflake}, ${response?.expires}, ${response?.verified}")
            } else if (nextLine.startsWith(">>unlink ")) {
                KameraClient.get().withService<LinkService>()
                    .unlink(Uuid.parseHex(nextLine.replace(">>unlink ", "")))
            } else {
                println(
                    KameraClient.get().withService<CommandService>()
                        .run(nextLine)
                )
            }
        } catch (e: Throwable) {
            println("Error: $e")
        }
    }
}