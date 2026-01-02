@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.api

import gs.mclo.api.response.InsightsResponse
import gs.mclo.api.response.UploadLogResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.tassia.diskord.webhook.DiscordWebhook
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.compat.modifyToNegateMarkdown
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.DiscordSpec
import java.nio.file.Path
import kotlin.uuid.ExperimentalUuidApi

suspend fun DiscordWebhook.sendLiteral(message: String, useEmbeds: Boolean) = execute {
    if (useEmbeds) embed {
        color = globalConfig[DiscordSpec.embedColor]
        footer(message, "TODO")
    } else {
        content = message
    }
    content += "" // Ensure at least something gets sent
}

suspend fun DiscordWebhook.sendChatMessage(message: String, player: McPlayer, useEmbed: Boolean) = execute {
    if (useEmbed) embed {
        color = globalConfig[DiscordSpec.embedColor]
        footer("${player.name}: $message", "https://mc-heads.net/avatar/${player.uuid}/90")
    } else {
        content = "**${player.name.modifyToNegateMarkdown()}**: $message"
    }
    content += "" // Ensure at least something gets sent
}

suspend fun DiscordWebhook.sendCrashMessage(error: Throwable, log: Path) {
    val response: UploadLogResponse? = withContext(Dispatchers.IO) {
        KhatMod.MCL.uploadLog(log).get()
    }
    val insights: InsightsResponse? = withContext(Dispatchers.IO) {
        response?.insights?.get()
    }
    execute {
        embed {
            title = "Minecraft experienced an exception!"
            color = 7864320
            field("Error", response?.error ?: error.message ?: "Unknown error", true)
            if (response != null) {
                field("Full log", "[[`${response.id}`](${response.url})]", true)
            }
            if (insights != null) {
                field("Insights", """
                    **Problems**
                    ${insights.analysis.problems.joinToString("\n") { analysis ->
                    "${analysis.message} (${analysis.counter}x, ${analysis.entry.level.name}):" +
                        analysis.solutions.joinToString("\n", ">>Solutions:") { solution -> 
                            ">>_${solution.message}_"
                        }
                }}
                    **Information**
                    ${insights.analysis.information.joinToString("\n") { information -> 
                        "${information.label}: ${information.value}(${information.counter}x)"
                }}""".trimIndent())
            }
        }
    }
}