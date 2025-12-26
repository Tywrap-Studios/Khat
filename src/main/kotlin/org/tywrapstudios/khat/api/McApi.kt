package org.tywrapstudios.khat.api

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class McPlayer @OptIn(ExperimentalUuidApi::class) constructor(
    val name: String,
    val uuid: String
)