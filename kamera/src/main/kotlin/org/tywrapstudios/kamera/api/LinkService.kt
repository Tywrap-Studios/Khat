@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Rpc
interface LinkService {
    suspend fun generateCode(uuid: Uuid, snowflake: ULong): LinkCode
    suspend fun getLinkStatus(uuid: Uuid): LinkStatus
    suspend fun getLinkStatus(snowflake: ULong): LinkStatus
    suspend fun attemptVerification(uuid: Uuid, code: String): VerificationResult
    suspend fun forceVerification(uuid: Uuid): VerificationResult
    suspend fun forceVerification(snowflake: ULong): VerificationResult
}

@Serializable
data class LinkStatus(
    val uuid: Uuid,
    val snowflake: ULong,
    val verified: Boolean,
)

@Serializable
data class VerificationResult(
    val success: Boolean,
    val reason: String?,
)

data class LinkCode(
    val code: String,
    val expires: Instant,
)