//? if krpc {
package org.tywrapstudios.khat.platform.kamera

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.ChatVisiblity
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.khat.KhatMod
import java.net.URI

object ChatServiceImpl : ChatService {
    override suspend fun sendMessage(data: ChatService.MessageData) {
        for (player in KhatMod.SERVER.playerList.players) {
            if (player.chatVisibility != ChatVisiblity.FULL) {
                continue
            }

            val type = data.type.toComponent(data.replyTo)
            val name = data.author.toComponent()
            val content = data.toComponent()

            player.sendSystemMessage(
                type.append(name.append(Component.literal(" ").append(content)))
            )
        }
    }

    private fun ChatService.MessageType.toComponent(replyTo: ChatService.UserData? = null): MutableComponent {
        val component = Component.empty()

        when (this) {
            ChatService.MessageType.TEXT -> {}

            ChatService.MessageType.REPLY -> {
                component.append(
                    Component
                        .literal("Replied to ")
                        .withStyle(ChatFormatting.GRAY)
                ).append(
                    replyTo!!.toComponent().append("\n")
                )
            }

            ChatService.MessageType.FORWARD -> {
                component.append(
                    Component
                        .literal("Forwarded\n")
                        .withStyle(ChatFormatting.GRAY)
                )
            }

            ChatService.MessageType.JUST_EMBED -> {
                component.append(
                    Component
                        .literal("Sent an embed\nDescription:\n")
                        .withStyle(ChatFormatting.GRAY)
                )
            }

            ChatService.MessageType.OTHER -> {
                component.append(
                    Component
                        .literal("Sent a message that could not be displayed.\n")
                        .withStyle(ChatFormatting.GRAY)
                )
            }
        }
        return component
    }

    private fun ChatService.UserData.toComponent(): MutableComponent {
        return Component
            .literal("<@$displayName>")
            .setStyle(
                Style.EMPTY
                    .withClickEvent(
                        //? if >=1.21.11 {
                        ClickEvent.SuggestCommand("<@$id>")
                        //?} else {
                        /*ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "<@$id>")
                        *///?}
                    )
                    .withHoverEvent(
                        //? if >=1.21.11 {
                        HoverEvent.ShowText(
                            Component.literal("($username) (Click to mention)").withStyle(ChatFormatting.BLUE)
                        )
                        //?} else {
                        /*HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Component.literal("($username) (Click to mention)").withStyle(ChatFormatting.BLUE)
                        )
                        *///?}
                    )
                    .withColor(ChatFormatting.BLUE)
            )
    }

    private fun ChatService.MessageData.toComponent(): MutableComponent {
        val component = Component.empty()

        if (type == ChatService.MessageType.OTHER) {
            component.append(
                Component
                    .literal("Go to message.")
                    .withStyle(
                        Style.EMPTY
                            .withClickEvent(
                                //? if >=1.21.11 {
                                ClickEvent.OpenUrl(URI(messageUrl))
                                //?} else {
                                /*ClickEvent(ClickEvent.Action.OPEN_URL, messageUrl)
                                *///?}
                            )
                            .withHoverEvent(
                                //? if >=1.21.11 {
                                HoverEvent.ShowText(Component.literal("Open in app").withStyle(ChatFormatting.GRAY))
                                //?} else {
                                /*HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("Open in app").withStyle(ChatFormatting.GRAY)
                                )
                                *///?}
                            )
                            .withUnderlined(true)
                            .withColor(ChatFormatting.DARK_AQUA)
                    )
            )
            return component
        }

        val splits = message.split("(?<=<@\\d*>)|(?=<@\\d*>)".toRegex())

        KhatMod.LOGGER.info("Splits: $splits")
        KhatMod.LOGGER.info("Pings: $pings")

        splits.forEach {
            if (it.matches("<@\\d*>".toRegex())) {
                val id = it.removePrefix("<@").removeSuffix(">").toULong()
                val user = pings.find { user -> user.id == id }
                if (user != null) {
                    component.append(user.toComponent())
                } else {
                    component.append(it)
                }
            } else {
                component.append(Component.literal(it).withStyle(ChatFormatting.GRAY))
            }
        }

        return component
    }
}
//?}