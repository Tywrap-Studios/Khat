package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object KhatSpec : ConfigSpec() {
    val version by optional("3.0", description = "This is an internal value that should not be touched!")

    object DiscordSpec : ConfigSpec() {
        val webhooks by optional(mutableListOf<String>(),
            description = "A list of webhooks in Strings that the mod will send messages to: \"https://discord.com/api/webhooks/...\"")
        val onlyMessages by optional(false,
            description = "Whether to only send player messages to Discord, and not game related messages (e.g. join/leave messages, deaths, etc.).")
        val embedMode by optional(false,
            description = "Whether to send messages as an embed. If false, messages will be sent as plain text.")
        val embedColor by optional(5489270,
            description = """The setting below must be an RGB int, so not a `255, 255, 255` type of thing.
                Use this site if you want to use this feature:
                http://www.shodor.org/~efarrow/trunk/html/rgbint.html""".trimIndent())
        val roles by optional(mutableListOf<String>(),
            description = "A list of role ID's in Strings that users are allowed to ping from MC. e.g. \"123456789012345678\"")
    }
}