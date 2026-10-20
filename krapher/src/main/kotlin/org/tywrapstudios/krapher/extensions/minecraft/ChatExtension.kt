package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.MessageReferenceType
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.route.Route
import dev.kordex.core.checks.inChannel
import dev.kordex.core.checks.isNotBot
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.getJumpUrl
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.api.getPlayer

class ChatExtension : Extension() {
    override val name: String = "krapher.minecraft.chat"

    @OptIn(KordExperimental::class, KordUnsafe::class)
    override suspend fun setup() {
        event<MessageCreateEvent> {
            check {
                inChannel(Snowflake(BotInitializer.config.chat))
                isNotBot()
                passIf(event.message.author?.isBot == false && event.message.author?.isBot != null)
            }

            action {
                val member = event.member ?: return@action
                if (member.isBot) {
                    return@action
                }
                if (event.message.channelId != Snowflake(BotInitializer.config.chat)) {
                    return@action
                }
                val player = getPlayer(member)
                val name = player.getName()

                val message = kord.rest.unsafe(Route.MessageGet) {
                    keys[Route.ChannelId] = event.message.channelId
                    keys[Route.MessageId] = event.message.id
                }

                val type = if (message.type == MessageType.Reply) {
                    ChatService.MessageType.REPLY
                } else if (message.messageReference.value?.type == MessageReferenceType.Forward) {
                    ChatService.MessageType.FORWARD
                } else if (message.embeds.isNotEmpty()) {
                    ChatService.MessageType.JUST_EMBED
                } else if (message.content.isNotEmpty()) {
                    ChatService.MessageType.TEXT
                } else {
                    ChatService.MessageType.OTHER
                }

                val content = when (type) {
                    ChatService.MessageType.REPLY, ChatService.MessageType.TEXT -> event.message.content
                    ChatService.MessageType.FORWARD -> message.messageSnapshots.value?.firstOrNull()?.message?.content
                    ChatService.MessageType.JUST_EMBED -> message.embeds.joinToString("\n\n") {
                        it.description.value ?: ""
                    }
                    ChatService.MessageType.OTHER -> ""
                }

                val replyAuthor = event.message.referencedMessage?.author
                val replyTo = if (replyAuthor != null) {
                    val replyPlayer = getPlayer(replyAuthor.asMember(event.guildId!!))
                    ChatService.UserData(
                        replyPlayer.getName(),
                        replyAuthor.username,
                        replyAuthor.id.value,
                    )
                } else {
                    null
                }

                val data = ChatService.MessageData(
                    ChatService.UserData(name, member.username, member.id.value),
                    content!!,
                    event.message.getJumpUrl(),
                    event.message.mentionedUsers.map {
                        val itPlayer = getPlayer(it.asMember(event.guildId!!))
                        ChatService.UserData(
                            itPlayer.getName(),
                            it.username,
                            it.id.value
                        )
                    }.toList(),
                    type,
                    replyTo
                )

                KameraClient.get().withService<ChatService>()
                    .sendMessage(data)
            }
        }
    }
}