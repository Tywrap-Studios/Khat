@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.database.sql

import org.jetbrains.exposed.v1.core.ColumnTransformer
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp
import java.util.*
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class KUuidTransformer : ColumnTransformer<UUID, Uuid> {
    override fun unwrap(value: Uuid): UUID = value.toJavaUuid()

    override fun wrap(value: UUID): Uuid = value.toKotlinUuid()
}

fun Table.kUuid(name: String) = uuid(name).transform(KUuidTransformer())

class KInstantTransformer : ColumnTransformer<java.time.Instant, Instant> {
    override fun unwrap(value: Instant): java.time.Instant = value.toJavaInstant()

    override fun wrap(value: java.time.Instant): Instant = value.toKotlinInstant()
}

fun Table.kTimestamp(name: String) = timestamp(name).transform(KInstantTransformer())
