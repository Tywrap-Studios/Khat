package org.tywrapstudios.khat.platform.kamera

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.ChatVisiblity
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.khat.KhatMod

object ChatServiceImpl : ChatService {
    override suspend fun sendMessage(name: String, message: String) {
        for (player in KhatMod.SERVER.playerList.players) {
            if (player.chatVisibility != ChatVisiblity.FULL) {
                continue
            }
            player.sendSystemMessage(
                Component
                    .literal("$name: ")
                    .withStyle(ChatFormatting.BLUE)
                    .append(
                        Component
                            .literal(message)
                            .withStyle(ChatFormatting.RESET)
                    ),
            )
        }
    }
}