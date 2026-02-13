@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.logic

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.api.sendChatMessage
import org.tywrapstudios.khat.api.sendCrashMessage
import org.tywrapstudios.khat.api.sendLiteral
import org.tywrapstudios.khat.config.WebhookSpec
import org.tywrapstudios.khat.config.webhooks
import java.nio.file.Path

object HandleMinecraft {
    fun handleChatMessage(message: String, player: McPlayer) = GlobalScope.launch {
        webhooks.forEach {
            if (it.config[WebhookSpec.onlyMessages] && player.uuid == "console") return@launch
            val message = message.handleAll(it.config)
            it.sendChatMessage(message, player)
        }
    }

    fun handleGameMessage(message: String) = GlobalScope.launch {
        webhooks.forEach {
            if (it.config[WebhookSpec.onlyMessages]) return@launch
            var message = message.handleAll(it.config)
            if (!it.config[WebhookSpec.useEmbeds]) {
                message = "**$message**"
            }
            it.sendLiteral(message)
        }
    }

    fun handleCrash(error: Throwable, report: Path) = GlobalScope.launch {
        webhooks.forEach {
            if (it.config[WebhookSpec.onlyMessages]) return@forEach
            it.sendCrashMessage(error, report)
        }
    }
}