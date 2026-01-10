@file:OptIn(DelicateCoroutinesApi::class)

package org.tywrapstudios.khat.compat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tywrapstudios.khat.api.ConfiguredWebhook
import org.tywrapstudios.khat.config.WebhookSpec
import org.tywrapstudios.khat.config.webhooks
import java.util.concurrent.TimeoutException

fun handleSparkWorldTimeOut(e: TimeoutException) = GlobalScope.launch {
    webhooks
        .forEach {
            if (it.config[WebhookSpec.onlyMessages]) return@forEach
            it.sendTimeOutMessage()
        }
    e.printStackTrace()
}

private suspend fun ConfiguredWebhook.sendTimeOutMessage() = webhook.execute {
    embed {
        title = "Spark Profiler"
        rgb(7864320)
        description = """
                **Timed out waiting for world statistics.**
                **Stacktrace:**
                -> View your console logs for more information."""
    }
}