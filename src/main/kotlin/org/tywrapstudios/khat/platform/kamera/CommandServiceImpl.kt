package org.tywrapstudios.khat.platform.kamera

import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.khat.KhatMod

class CommandServiceImpl : CommandService {
    override suspend fun run(command: String) {
        var command = command
        if (command.startsWith("/")) {
            command = command.replaceFirst("/", "")
        }
        val source = KhatMod.SERVER.createCommandSourceStack()
        KhatMod.SERVER.commands.dispatcher.execute(command, source)
    }
}