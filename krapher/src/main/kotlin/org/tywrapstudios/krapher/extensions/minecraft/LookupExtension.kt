@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.builder.components.emoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.member
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.api.McPlayer
import org.tywrapstudios.krapher.api.getMcPlayer
import org.tywrapstudios.krapher.i18n.Translations
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LookupExtension : Extension() {
    override val name: String = "krapher.minecraft.lookup"

    override suspend fun setup() {
        publicSlashCommand {
            name = Translations.Commands.Profiles.name
            description = Translations.Commands.Profiles.description

            publicSubCommand(::LookupCommandArguments) {
                name = Translations.Commands.Profiles.Lookup.name
                description = Translations.Commands.Profiles.Lookup.description
                action {
                    val mcLink = try {
                        KameraClient.get().withService<LinkService>()
                            .getLinkStatusBySnowflake(arguments.member.id.value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    val player = if (mcLink != null ) getMcPlayer(mcLink.uuid) else null
                    if (mcLink != null && player != null) {
                        respond {
                            embed {
                                title = "Minecraft profile for ${arguments.member.effectiveName}"
                                field {
                                    name = "Username and UUID"
                                    value = """
										```
										${player.name}
										```
										```
										${Uuid.parse(player.id).toHexDashString()}
										```
									""".trimIndent()
                                }
                                field {
                                    name = "Link status"
                                    value = "Verified: ${if (mcLink.verified) "✅" else "❌"}"
                                }
                                thumbnail {
                                    url = "https://mc-heads.net/avatar/${mcLink.uuid}/90"
                                }
                                footer {
                                    text = player.name
                                    icon = "https://mc-heads.net/avatar/${mcLink.uuid}/90"
                                }
                            }
                            actionRow {
                                interactionButton(ButtonStyle.Primary, "minecraft:force-link") {
                                    label = "Force link"
                                    emoji(ReactionEmoji.Unicode("🔗"))
                                }
                            }
                        }
                    } else {
                        respond {
                            embed {
                                title = "Minecraft profile for ${arguments.member.effectiveName}"
                                field {
                                    name = "Could not present profile:"
                                    value = if (mcLink == null)
                                        "Our database does not contain this member."
                                    else if (player == null) "Our database contains a linked UUID, but this player profile " +
                                            "does not actually exist or could not be found due to other reasons."
                                    else "Something unexpected happened, please contact a staff member."
                                }
                            }
                        }
                    }
                }
            }

            publicSubCommand(::SearchUuidArguments) {
                name = Translations.Commands.Profiles.SearchUuid.name
                description = Translations.Commands.Profiles.SearchUuid.description

                action {
                    val player = getMcPlayer(Uuid.parse(arguments.uuid))
                    respond {
                        mcPlayerProfileEmbed(arguments.uuid, player)
                    }
                }
            }

            publicSubCommand(::SearchUsernameArguments) {
                name = Translations.Commands.Profiles.SearchUsername.name
                description = Translations.Commands.Profiles.SearchUsername.description

                action {
                    val player = getMcPlayer(arguments.username)
                    respond {
                        mcPlayerProfileEmbed(arguments.username, player)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun FollowupMessageCreateBuilder.mcPlayerProfileEmbed(prompt: String, player: McPlayer?) {
        val mcLink = try {
            KameraClient.get().withService<LinkService>()
                .getLinkStatus(Uuid.parse(player?.id ?: ""))
        } catch (_: Exception) {
            null
        }

        if (player == null) {
            embed {
                title = "Minecraft profile for $prompt"
                field {
                    name = "Could not present profile:"
                    value = "This player profile does not actually exist or could not be found due to other reasons."
                }
                color = DISCORD_RED
            }
            return
        }
        embed {
            title = "Minecraft profile for $prompt"
            field {
                name = "Username and UUID"
                value = """
										```
										${player.name}
										```
										```
										${Uuid.parse(player.id).toHexDashString()}
										```
									""".trimIndent()
            }
            field {
                name = "Link status"
                value = if (mcLink == null) {
                    "This UUID is currently not being linked."
                } else {
                    """
					User: <@${mcLink.snowflake}>
					Verified: ${if (mcLink.verified) "✅" else "❌"}
					""".trimIndent()
                }
            }
            thumbnail {
                url = "https://mc-heads.net/body/${player.id}/600/left"
            }
            footer {
                text = player.name
                icon = "https://mc-heads.net/avatar/${player.id}/600"
            }
            color = DISCORD_BLURPLE
        }
        if (mcLink != null) {
            actionRow {
                interactionButton(ButtonStyle.Primary, "minecraft:force-link") {
                    label = "Force link"
                    emoji(ReactionEmoji.Unicode("🔗"))
                }
            }
        }
    }

    class LookupCommandArguments : Arguments() {
        val member by member {
            name = Translations.Args.Profiles.Lookup.Member.name
            description = Translations.Args.Profiles.Lookup.Member.description
        }
    }

    class SearchUuidArguments : Arguments() {
        val uuid by string {
            name = Translations.Args.Profiles.SearchUuid.Uuid.name
            description = Translations.Args.Profiles.SearchUuid.Uuid.description
        }
    }

    class SearchUsernameArguments : Arguments() {
        val username by string {
            name = Translations.Args.Profiles.SearchUsername.Username.name
            description = Translations.Args.Profiles.SearchUsername.Username.description
        }
    }
}