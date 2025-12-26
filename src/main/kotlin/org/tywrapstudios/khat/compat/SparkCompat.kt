@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.compat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.tassia.diskord.webhook.DiscordWebhook
import net.tassia.diskord.webhook.Webhook
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.config.KhatSpec
import java.util.concurrent.TimeoutException

fun handleSparkWorldTimeOut(e: TimeoutException) = GlobalScope.launch {
    KhatMod.GLOBAL_CONFIG[KhatSpec.DiscordSpec.webhooks]
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