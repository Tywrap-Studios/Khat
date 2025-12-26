package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec
import net.tassia.diskord.webhook.DiscordWebhook

object KhatSpec : ConfigSpec() {
    val version by optional("3.0")

    object DiscordSpec : ConfigSpec() {
        val webhooks by optional(mutableListOf<String>())
        val onlyMessages by optional(false)
        val embedMode by optional(false)
        val embedColor by optional(5489270)
        val roles by optional(mutableListOf<String>())
    }
}