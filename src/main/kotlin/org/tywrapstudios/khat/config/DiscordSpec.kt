package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object DiscordSpec : ConfigSpec() {
    val webhooks by required<MutableList<String>>(
        description = "A list of webhooks in Strings that the mod will send messages to: \"https://discord.com/api/webhooks/...\"")
    val onlyMessages by required<Boolean>(
        description = "Whether to only send player messages to Discord, and not game related messages (e.g. join/leave messages, deaths, etc.).")
    val embedMode by required<Boolean>(
        description = "Whether to send messages as an embed. If false, messages will be sent as plain text.")
    val embedColor by required<Int>(
        description = """The setting below must be an RGB int, so not a `255, 255, 255` type of thing.
                Use this site if you want to use this feature:
                http://www.shodor.org/~efarrow/trunk/html/rgbint.html""".trimIndent())
    val roles by required<MutableList<String>>(
        description = "A list of role ID's in Strings that users are allowed to ping from MC. e.g. \"123456789012345678\"")
}