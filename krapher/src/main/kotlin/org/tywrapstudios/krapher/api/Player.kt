@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.krapher.api

import dev.kord.core.entity.Member
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.krapher.KameraClient
import kotlin.uuid.ExperimentalUuidApi

data class Player(
    val member: Member,
    val mcPlayer: McPlayer?
) {
    fun getName(): String {
        return mcPlayer?.name ?: member.effectiveName
    }
}

suspend fun getPlayer(member: Member): Player {
    val result = try {
        KameraClient.get().withService<LinkService>()
            .getLinkStatusBySnowflake(member.id.value)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    val player = if (result != null ) getMcPlayer(result.uuid) else null

    return Player(
        member,
        player
    )
}
