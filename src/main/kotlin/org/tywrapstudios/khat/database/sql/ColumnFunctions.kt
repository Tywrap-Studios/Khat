//? if krpc {
@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.database.sql
import org.jetbrains.exposed.v1.core.ColumnTransformer
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi

fun Table.kUuid(name: String) = uuid(name)

class KInstantTransformer : ColumnTransformer<java.time.Instant, Instant> {
    override fun unwrap(value: Instant): java.time.Instant = value.toJavaInstant()

    override fun wrap(value: java.time.Instant): Instant = value.toKotlinInstant()
}

fun Table.kTimestamp(name: String) = timestamp(name).transform(KInstantTransformer())
//?}