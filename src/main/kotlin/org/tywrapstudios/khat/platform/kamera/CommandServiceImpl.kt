package org.tywrapstudios.khat.platform.kamera

import net.minecraft.server.dedicated.DedicatedServer
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.khat.KhatMod

class CommandServiceImpl : CommandService {
    override suspend fun run(command: String): String {
        var command = command
        if (command.startsWith("/")) {
            command = command.replaceFirst("/", "")
        }
        if (KhatMod.SERVER.isDedicatedServer) {
            val server = KhatMod.SERVER as DedicatedServer
            return server.runCommand(command).ifEmpty { "No or empty response" }
        }
        return "Server is not dedicated"
    }
}