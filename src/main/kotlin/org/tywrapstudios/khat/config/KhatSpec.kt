package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object KhatSpec : ConfigSpec() {
    val version by optional("3.0",
        description = "This is an internal value that should not be touched!")
}