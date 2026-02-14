package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.checks.inChannel
import dev.kordex.core.checks.isNotBot
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.api.getPlayer

class ChatExtension : Extension() {
    override val name: String = "krapher.minecraft.chat"

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check { inChannel(Snowflake(BotInitializer.config.chat)) }
            check { isNotBot() }

            action {
                val player = if (event.member != null) getPlayer(event.member!!) else null
                val name = player?.getName() ?: "Discord Member"
                KameraClient.get().withService<ChatService>()
                    .sendMessage(name, event.message.content)
            }
        }
    }
}