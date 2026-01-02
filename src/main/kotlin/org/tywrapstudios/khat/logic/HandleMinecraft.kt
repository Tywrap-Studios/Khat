@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.logic

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.api.sendChatMessage
import org.tywrapstudios.khat.api.sendCrashMessage
import org.tywrapstudios.khat.api.sendLiteral
import org.tywrapstudios.khat.config.DiscordSpec
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.webhooks
import java.nio.file.Path

object HandleMinecraft {
    fun handleChatMessage(message: String, player: McPlayer) = GlobalScope.launch {
        if (globalConfig[DiscordSpec.onlyMessages] && player.uuid == "console") return@launch
        val message = message.handleAll()
        webhooks.forEach {
            it.sendChatMessage(message, player, globalConfig[DiscordSpec.embedMode])
        }
    }

    fun handleGameMessage(message: String) = GlobalScope.launch {
        if (globalConfig[DiscordSpec.onlyMessages]) return@launch
        var message = message.handleAll()
        val useEmbeds = globalConfig[DiscordSpec.embedMode]
        if (globalConfig[DiscordSpec.embedMode]) {
            message = "**$message**"
        }
        webhooks.forEach {
            it.sendLiteral(message, useEmbeds)
        }
    }

    fun handleCrash(error: Throwable, report: Path) = GlobalScope.launch {
        if (globalConfig[DiscordSpec.onlyMessages]) return@launch
        webhooks.forEach {
            it.sendCrashMessage(error, report)
        }
    }
}