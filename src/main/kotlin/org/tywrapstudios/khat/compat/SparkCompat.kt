@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.compat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.tassia.diskord.webhook.DiscordWebhook
import net.tassia.diskord.webhook.Webhook
import org.tywrapstudios.khat.config.DiscordSpec
import org.tywrapstudios.khat.config.globalConfig
import java.util.concurrent.TimeoutException

fun handleSparkWorldTimeOut(e: TimeoutException) = GlobalScope.launch {
    if (globalConfig[DiscordSpec.onlyMessages]) return@launch
    globalConfig[DiscordSpec.webhooks]
        .map { Webhook(it) }
        .forEach {
            it.sendTimeOutMessage()
        }
    e.printStackTrace()
}

private suspend fun DiscordWebhook.sendTimeOutMessage() = execute {
    embed {
        title = "Spark Profiler"
        color = 7864320
        description = """
                **Timed out waiting for world statistics.**
                **Stacktrace:**
                -> View your console logs for more information."""
    }
}