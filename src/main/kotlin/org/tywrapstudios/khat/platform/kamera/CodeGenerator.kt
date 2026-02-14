package org.tywrapstudios.khat.platform.kamera

import java.security.SecureRandom

object CodeGenerator {
    private val secureRandom = SecureRandom()
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun generateCode(): String {
        var code = ""
        for (n in 1..16) {
            code += if (secureRandom.nextBoolean()) {
                secureRandom.nextInt(0, 10)
            } else {
                CHARS[secureRandom.nextInt(0, CHARS.length)]
            }
        }
        return code
    }
}