package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.builder.components.emoji
import dev.kord.core.entity.Member
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.checks.guildFor
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.scheduling.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ServerStatsService
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.KameraClient
import org.tywrapstudios.krapher.api.getPlayer
import org.tywrapstudios.krapher.checks.isModerator
import org.tywrapstudios.krapher.i18n.Translations
import kotlin.time.Duration.Companion.seconds

class MiscExtension : Extension() {
    override val name: String = "krapher.minecraft.misc"

    var statusTask: Task? = null
    var updateTask: Task? = null

    override suspend fun setup() {
        statusTask = BotInitializer.scheduler.schedule(
            20.seconds,
            name = "Status Refresher Task",
            repeat = true
        ) {
            val triple = try {
                val client = KameraClient.get().withService<ServerStatsService>()
                Triple(client.isOnline(), client.playerCount(), client.maximumPlayers())
            } catch (e: Exception) {
                e.printStackTrace()
                Triple(false, 0, 0)
            }
            kord.editPresence {
                this.status = if (triple.first) PresenceStatus.Online else PresenceStatus.Idle
                watching("${triple.second}/${triple.third} people playing.")
            }
        }

        event<MemberUpdateEvent> {
            check {
                anyGuild()

            }

            action {
                if (BotInitializer.config.enforceUsernames) {
                    val member = event.member
                    val player = getPlayer(member)
                    if (player.mcPlayer == null) {
                        return@action
                    }
                    val shouldBe = BotInitializer.config.usernamePattern
                        .replace($$"$displayName", member.globalName ?: member.username)
                        .replace($$"$minecraft", player.mcPlayer.name)
                    if (member.effectiveName == shouldBe) {
                        return@action
                    } else {
                        val new = if (shouldBe.length > 32) player.mcPlayer.name else shouldBe
                        member.edit {
                            nickname = new
                            reason = "Enforcing usernames"
                        }
                    }
                }
            }
        }

        ephemeralSlashCommand {
            name = Translations.Commands.Misc.name
            description = Translations.Commands.Misc.description

            ephemeralSubCommand {
                name = Translations.Commands.Misc.UpdateProfiles.name
                description = Translations.Commands.Misc.UpdateProfiles.description

                check { isModerator() }

                action {
                    respond {
                        embed {
                            title = Translations.Responses.Misc.UpdateProfiles.WarnEmbed.title.translate()
                            description = Translations.Responses.Misc.UpdateProfiles.WarnEmbed.description.translate()
                        }
                        actionRow {
                            interactionButton(ButtonStyle.Danger, "misc:update-profiles") {
                                label = Translations.Responses.Misc.UpdateProfiles.WarnEmbed.Button.label.translate()
                                emoji(ReactionEmoji.Unicode("▶️"))
                            }
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Commands.Misc.CancelUpdates.name
                description = Translations.Commands.Misc.CancelUpdates.description

                action {
                    updateTask?.cancel()
                    respond {
                        embed {
                            title = Translations.Responses.Misc.CancelUpdates.Embed.title.translate()
                            description = Translations.Responses.Misc.CancelUpdates.Embed.description.translate()
                        }
                    }
                }
            }
        }

        event<ButtonInteractionCreateEvent> {
            check {
                anyGuild()
                failIf(event.interaction.componentId != "misc:update-profiles")
            }

            action {
                event.interaction.respondPublic {
                    content = Translations.Responses.Misc.UpdateProfiles.starting.translate()
                }
                val channel = event.interaction.channel
                val guild = guildFor(event)!!.asGuild()

                updateTask = BotInitializer.scheduler.schedule(
                    0.seconds,
                    name = "Update Profiles Task",
                    startNow = false
                ) {
                    val members = guild.members
                    withContext(Dispatchers.Default) {
                        members
                            .onCompletion {
                                channel.createMessage(
                                    Translations.Responses.Misc.UpdateProfiles.end.translate(),
                                )
                            }
                            .collect { member ->
                                collection(channel, member)
                            }
                    }
                }
                updateTask?.start()
            }
        }
    }

    private suspend fun collection(
        channel: MessageChannelBehavior,
        member: Member
    ) {
        val originalMessage = channel.createMessage(
            Translations.Responses.Misc.UpdateProfiles.checking.withOrdinalPlaceholders(
                member.username
            ).translate()
        )
        try {
            if (BotInitializer.config.enforceUsernames) {
                val player = getPlayer(member)
                if (player.mcPlayer == null) {
                    originalMessage.edit {
                        content =
                            Translations.Responses.Misc.UpdateProfiles.Checking.Fine.notLinked.withOrdinalPlaceholders(
                                member.username
                            ).translate()
                    }
                    return
                }
                val shouldBe = BotInitializer.config.usernamePattern
                    .replace($$"$displayName", member.globalName ?: member.username)
                    .replace($$"$minecraft", player.mcPlayer.name)
                if (member.effectiveName == shouldBe) {
                    originalMessage.edit {
                        content =
                            Translations.Responses.Misc.UpdateProfiles.Checking.Fine.followsPattern.withOrdinalPlaceholders(
                                member.username
                            ).translate()
                    }
                    return
                } else {
                    originalMessage.edit {
                        content =
                            Translations.Responses.Misc.UpdateProfiles.Checking.needsUpdate.withOrdinalPlaceholders(
                                member.username
                            ).translate()
                    }
                    val before = member.effectiveName
                    val new = if (shouldBe.length > 32) player.mcPlayer.name else shouldBe
                    member.edit {
                        nickname = new
                        reason = "Enforcing usernames"
                    }
                    originalMessage.edit {
                        content = Translations.Responses.Misc.UpdateProfiles.Checking.updated.withOrdinalPlaceholders(
                            member.username,
                            before,
                            new,
                            shouldBe
                        ).translate()
                    }
                }
            } else {
                originalMessage.edit {
                    content =
                        Translations.Responses.Misc.UpdateProfiles.Checking.Fine.noRestrictions.withOrdinalPlaceholders(
                            member.username
                        ).translate()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            originalMessage.edit {
                content =
                    Translations.Responses.Misc.UpdateProfiles.Checking.error.withOrdinalPlaceholders(
                        member.username
                    ).translate()
            }
            return
        }
    }
}