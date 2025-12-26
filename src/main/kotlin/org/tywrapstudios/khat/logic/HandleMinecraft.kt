@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.logic

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.api.sendChatMessage
import org.tywrapstudios.khat.api.sendCrashMessage
import org.tywrapstudios.khat.api.sendLiteral
import org.tywrapstudios.khat.config.KhatSpec
import java.nio.file.Path

object HandleMinecraft {
    fun handleChatMessage(message: String, player: McPlayer) = GlobalScope.launch {
        val message = message.handleAll()
        KhatMod.WEBHOOKS.forEach {
            it.sendChatMessage(message, player, KhatMod.GLOBAL_CONFIG[KhatSpec.DiscordSpec.embedMode])
        }
    }

    fun handleGameMessage(message: String) = GlobalScope.launch {
        var message = message.handleAll()
        val useEmbeds = KhatMod.GLOBAL_CONFIG[KhatSpec.DiscordSpec.embedMode]
        if (KhatMod.GLOBAL_CONFIG[KhatSpec.DiscordSpec.embedMode]) {
            message = "**$message**"
        }
        KhatMod.WEBHOOKS.forEach {
            it.sendLiteral(message, useEmbeds)
        }
    }

    fun handleCrash(error: Throwable, report: Path) = GlobalScope.launch {
        KhatMod.WEBHOOKS.forEach {
            it.sendCrashMessage(error, report)
        }
    }
}