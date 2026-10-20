@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Rpc
interface ChatService {
    @Serializable
    data class MessageData(
        val author: UserData,
        val message: String,
        val messageUrl: String,
        val pings: List<UserData>,
        val type: MessageType,
        val replyTo: UserData? = null,
    )

    @Serializable
    data class UserData(
        val displayName: String,
        val username: String,
        val id: ULong,
    )

    @Serializable
    enum class MessageType {
        TEXT,
        REPLY,
        FORWARD,
        JUST_EMBED,
        OTHER
    }

    suspend fun sendMessage(data: MessageData)
}