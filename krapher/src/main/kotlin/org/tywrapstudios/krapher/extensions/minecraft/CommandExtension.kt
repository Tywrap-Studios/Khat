package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.types.InteractionContext
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.checks.isModerator
import org.tywrapstudios.krapher.i18n.Translations

class CommandExtension : Extension() {
    override val name: String = "krapher.minecraft.cmd"

    override suspend fun setup() {
        publicSlashCommand {
            name = Translations.Commands.Cmd.name
            description = Translations.Commands.Cmd.description

            publicSubCommand {
                name = Translations.Commands.Cmd.List.name
                description = Translations.Commands.Cmd.List.description

                action {
                    run("/list")
                }
            }

            ephemeralSubCommand(::RunArguments) {
                name = Translations.Commands.Cmd.Run.name
                description = Translations.Commands.Cmd.Run.description

                check { isModerator() }

                action {
                    val command = arguments.command
                    run(command)
                }
            }
        }
    }

    class RunArguments : Arguments() {
        val command by string {
            name = Translations.Args.Cmd.Run.Command.name
            description = Translations.Args.Cmd.Run.Command.description
        }
    }

    suspend fun InteractionContext<*, *, *, *>.run(command: String) {
        respond {
            val response = try {
                KameraClient.get().withService<CommandService>()
                    .run(command)
            } catch (e: Exception) {
                e.printStackTrace()
                e.message
            }

            embed {
                color = DISCORD_BLURPLE
                title = Translations.Responses.Cmd.Embed.title.translate()
                description = Translations.Responses.Cmd.Embed.description.withOrdinalPlaceholders(
                    response
                ).translate()
            }
        }
    }
}