package org.tywrapstudios.khat.config

import com.uchuhimo.konf.ConfigSpec

object RpcSpec : ConfigSpec() {
    val enabled by required<Boolean>()
    val port by required<Int>()
    val token by required<String>()

    object FeatureSpec : ConfigSpec() {
        val linking by required<Boolean>()
    }
}