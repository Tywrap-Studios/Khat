@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.logic

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.api.sendChatMessage
import org.tywrapstudios.khat.api.sendCrashMessage
import org.tywrapstudios.khat.api.sendLiteral
import org.tywrapstudios.khat.config.getGlobalConfig
import org.tywrapstudios.khat.config.KhatSpec
import org.tywrapstudios.khat.config.WEBHOOKS
import java.nio.file.Path

object HandleMinecraft {
    fun handleChatMessage(message: String, player: McPlayer) = GlobalScope.launch {
        val message = message.handleAll()
        WEBHOOKS.forEach {
            it.sendChatMessage(message, player, getGlobalConfig()[KhatSpec.DiscordSpec.embedMode])
        }
    }

    fun handleGameMessage(message: String) = GlobalScope.launch {
        var message = message.handleAll()
        val useEmbeds = getGlobalConfig()[KhatSpec.DiscordSpec.embedMode]
        if (getGlobalConfig()[KhatSpec.DiscordSpec.embedMode]) {
            message = "**$message**"
        }
        WEBHOOKS.forEach {
            it.sendLiteral(message, useEmbeds)
        }
    }

    fun handleCrash(error: Throwable, report: Path) = GlobalScope.launch {
        WEBHOOKS.forEach {
            it.sendCrashMessage(error, report)
        }
    }
}