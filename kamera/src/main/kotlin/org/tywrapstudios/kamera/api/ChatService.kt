@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc
import kotlin.uuid.ExperimentalUuidApi

@Rpc
interface ChatService {
    suspend fun sendMessage(name: String, username: String, id: ULong, message: String)
}