package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object WebhookSpec : ConfigSpec() {
    val onlyMessages by required<Boolean>()
    val useEmbeds by required<Boolean>()
    val useComponents by required<Boolean>()
    val primaryColor by required<String>()
    val pingRoles by required<MutableList<String>>()
}