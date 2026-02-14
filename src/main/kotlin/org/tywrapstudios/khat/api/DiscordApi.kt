@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.api

import gs.mclo.api.response.InsightsResponse
import gs.mclo.api.response.UploadLogResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tywrapstudios.hookt.types.components.SeparatorComponent
import org.tywrapstudios.hookt.types.components.TextDisplayComponent
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.compat.modifyToNegateMarkdown
import org.tywrapstudios.khat.config.WebhookSpec
import java.nio.file.Path
import kotlin.uuid.ExperimentalUuidApi

suspend fun ConfiguredWebhook.sendLiteral(message: String) = webhook.execute {
    if (config[WebhookSpec.useEmbeds]) {
        embed {
            hex(config[WebhookSpec.primaryColor])
            footer(message)
        }
    } else if (config[WebhookSpec.useComponents]) {
        component<TextDisplayComponent> {
            content = message
        }
    } else {
        content = message
    }
}

suspend fun ConfiguredWebhook.sendChatMessage(message: String, player: McPlayer) = webhook.execute {
    if (config[WebhookSpec.useEmbeds]) {
        embed {
            hex(config[WebhookSpec.primaryColor])
            footer("${player.name}: $message", "https://mc-heads.net/avatar/${player.uuid}/90")
        }
    } else if (config[WebhookSpec.useComponents]) {
        component<TextDisplayComponent> {
            content = "**${player.name.modifyToNegateMarkdown()}**: $message"
        }
    } else {
        content = "**${player.name.modifyToNegateMarkdown()}**: $message"
    }
}

suspend fun ConfiguredWebhook.sendCrashMessage(error: Throwable, log: Path) {
    val response: UploadLogResponse? = withContext(Dispatchers.IO) {
        KhatMod.MCL.uploadLog(log).get()
    }
    val insights: InsightsResponse? = withContext(Dispatchers.IO) {
        response?.insights?.get()
    }
    webhook.execute {
        if (config[WebhookSpec.useComponents]) {
            component<TextDisplayComponent> {
                content = "# Minecraft experienced an exception!"
            }
            component<SeparatorComponent> {
                divider = true
            }
            component<TextDisplayComponent> {
                content = "**Error**: ${response?.error ?: error.message ?: "Unknown error"}"
            }
            if (response != null) {
                component<TextDisplayComponent> {
                    content = "**Full log**: [[`${response.id}`](${response.url})]"
                }
            }
            if (insights != null && (insights.analysis.problems.isNotEmpty() || insights.analysis.information.isNotEmpty())) {
                component<SeparatorComponent> {
                    divider = true
                }
                component<TextDisplayComponent> {
                    content = "# Insights"
                }
                if (insights.analysis.problems.isNotEmpty()) {
                    component<TextDisplayComponent> {
                        content = """## Problems
                    ${
                            insights.analysis.problems.joinToString("\n") { analysis ->
                                "${analysis.message} (${analysis.counter}x, ${analysis.entry.level.name})" + analysis.solutions.joinToString(
                                    "\n", ":\n### Solutions:"
                                ) { solution ->
                                    ">>_${solution.message}_"
                                }
                            }
                        }""".trimIndent()
                    }
                }
                if (insights.analysis.information.isNotEmpty()) {
                    component<TextDisplayComponent> {
                        content = """## Information
                    ${
                            insights.analysis.information.joinToString("\n") { information ->
                                "${information.label}: ${information.value}(${information.counter}x)"
                            }
                        }""".trimIndent()
                    }
                }
            }
        } else {
            embed {
                title = "Minecraft experienced an exception!"
                rgb(7864320)
                field {
                    name = "Error"
                    value = response?.error ?: error.message ?: "Unknown error"
                    inline = true
                }
                if (response != null) {
                    field {
                        name = "Full log"
                        value = "[[`${response.id}`](${response.url})]"
                        inline = true
                    }
                }
                if (insights != null && (insights.analysis.problems.isNotEmpty() || insights.analysis.information.isNotEmpty())) {
                    field {
                        name = "Insights"
                        val problems = """
                    **Problems**
                    ${
                            insights.analysis.problems.joinToString("\n") { analysis ->
                                "${analysis.message} (${analysis.counter}x, ${analysis.entry.level.name}):" + analysis.solutions.joinToString(
                                    "\n", ">>Solutions:"
                                ) { solution ->
                                    ">>_${solution.message}_"
                                }
                            }
                        }"""
                        val information = """**Information**
                    ${
                            insights.analysis.information.joinToString("\n") { information ->
                                "${information.label}: ${information.value}(${information.counter}x)"
                            }
                        }""".trimIndent()
                        if (insights.analysis.problems.isNotEmpty()) {
                            value += problems
                        }
                        if (insights.analysis.information.isNotEmpty()) {
                            value += information
                        }
                    }
                }
            }
        }
    }
}