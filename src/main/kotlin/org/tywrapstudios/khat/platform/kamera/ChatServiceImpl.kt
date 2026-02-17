//? if krpc {
package org.tywrapstudios.khat.platform.kamera

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.ChatVisiblity
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.khat.KhatMod

object ChatServiceImpl : ChatService {
    override suspend fun sendMessage(name: String, username: String, id: ULong, message: String) {
        for (player in KhatMod.SERVER.playerList.players) {
            if (player.chatVisibility != ChatVisiblity.FULL) {
                continue
            }
            val name = Component
                .literal("<@$name> ")
                .setStyle(
                    Style.EMPTY
                        .withClickEvent(
                            //? if 1.21.11 {
                            /*ClickEvent.SuggestCommand("<@$id>")
                            *///?} else {
                            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "<@$id>")
                            //?}
                        )
                        .withHoverEvent(
                            //? if 1.21.11 {
                            /*HoverEvent.ShowText(
                                Component.literal("($username) (Click to mention)").withStyle(ChatFormatting.BLUE)
                            )
                            *///?} else {
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("($username) (Click to mention)").withStyle(ChatFormatting.BLUE)
                            )
                            //?}
                        )
                        .withColor(ChatFormatting.BLUE)
                )
            val content = Component
                .literal(message)
                .withStyle(
                    Style.EMPTY
                        .withClickEvent(
                            //? if 1.21.11 {
                            /*ClickEvent.CopyToClipboard(message)
                            *///?} else {
                            ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)
                            //?}
                        )
                        .withHoverEvent(
                            //? if 1.21.11 {
                            /*HoverEvent.ShowText(Component.literal("Copy to Clipboard").withStyle(ChatFormatting.GRAY))
                            *///?} else {
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Copy to Clipboard").withStyle(ChatFormatting.GRAY)
                            )
                            //?}
                        )
                        .withColor(ChatFormatting.GRAY)
                )

            player.sendSystemMessage(
                name.append(content)
            )
        }
    }
}
//?}