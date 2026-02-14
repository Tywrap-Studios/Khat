package org.tywrapstudios.khat.config.migration.v2

import com.uchuhimo.konf.ConfigSpec

object V2Spec : ConfigSpec() {
    val formatVersion by required<String>("format_version")

    object DiscordSpec : ConfigSpec("discord_config") {
        val webhooks by required<MutableList<String>>("discord_webhooks")
        val onlyMessages by required<Boolean>("only_send_messages")
        val embedMode by required<Boolean>("embed_mode")
        val embedColorRgbInt by required<Int>("embed_color_rgb_int")
        val roleIds by required<MutableList<String>>("role_ids")
    }

    object UtilSpec : ConfigSpec("util_config") {
        val debugMode by required<Boolean>("debug_mode")
        val suppressWarns by required<Boolean>("suppress_warns")
    }
}