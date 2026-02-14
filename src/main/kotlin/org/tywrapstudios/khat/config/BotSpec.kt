package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object BotSpec : ConfigSpec() {
    val enabled by required<Boolean>()
    val token by required<String>()
    val channel by required<String>()
    val moderators by required<Set<String>>()
    val enforceUsername by required<Boolean>()
    val pattern by required<String>()
}