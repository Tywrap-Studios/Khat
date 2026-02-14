@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.member
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.time.TimestampType
import dev.kordex.core.time.toDiscord
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.checks.isModerator
import org.tywrapstudios.krapher.i18n.Translations
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LinkingExtension : Extension() {
    override val name: String = "krapher.minecraft.linking"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = Translations.Commands.Link.name
            description = Translations.Commands.Link.description

            check { anyGuild() }

            ephemeralSubCommand(::LinkArguments) {
                name = Translations.Commands.Link.New.name
                description = Translations.Commands.Link.New.description

                action {
                    val code = try {
                        KameraClient.get().withService<LinkService>()
                            .generateCode(Uuid.parse(arguments.uuid), member!!.id.value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    if (code == null) {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = Translations.Responses.Link.Embed.error.translate()
                                color = DISCORD_RED
                            }
                        }
                    } else {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = Translations.Responses.Link.New.Embed.description.withOrdinalPlaceholders(
                                    code.code,
                                    "/khat link verify ${code.code}",
                                    code.expires.toDiscord(TimestampType.ShortDateTime)
                                ).translate()
                                color = DISCORD_BLURPLE
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Commands.Link.Remove.name
                description = Translations.Commands.Link.Remove.description

                action {
                    val member = member!!.id.value

                    val result = try {
                        KameraClient.get().withService<LinkService>()
                            .unlinkBySnowflake(member)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    if (result == null || !result) {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = Translations.Responses.Link.Embed.error.translate()
                                color = DISCORD_RED
                            }
                        }
                    } else {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = Translations.Responses.Link.Remove.Embed.description.translate()
                                color = DISCORD_BLURPLE
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand(::ForceLinkArguments) {
                name = Translations.Commands.Link.ForceLink.name
                description = Translations.Commands.Link.ForceLink.description

                check { isModerator() }

                action {
                    val result = try {
                        KameraClient.get().withService<LinkService>()
                            .forceVerification(Uuid.parse(arguments.uuid), arguments.member.id.value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    if (result == null || !result.success) {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = Translations.Responses.Link.Embed.error.translate()
                                color = DISCORD_RED
                            }
                        }
                    } else {
                        respond {
                            embed {
                                title = Translations.Responses.Link.Embed.title.translate()
                                description = result.message
                                color = DISCORD_BLURPLE
                            }
                        }
                    }
                }
            }
        }
    }

    class LinkArguments : Arguments() {
        val uuid by string {
            name = Translations.Args.Link.New.Uuid.name
            description = Translations.Args.Link.New.Uuid.description
        }
    }

    class ForceLinkArguments : Arguments() {
        val uuid by string {
            name = Translations.Args.Link.ForceLink.Uuid.name
            description = Translations.Args.Link.ForceLink.Uuid.description
        }
        val member by member {
            name = Translations.Args.Link.ForceLink.Member.name
            description = Translations.Args.Link.ForceLink.Member.description
        }
    }
}