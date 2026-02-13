package org.tywrapstudios.krapher.checks

import dev.kordex.core.checks.memberFor
import dev.kordex.core.checks.types.CheckContext
import kotlinx.coroutines.flow.toList
import org.tywrapstudios.krapher.BotInitializer

suspend fun CheckContext<*>.isModerator() {
    if (!passed) {
        return
    }

    val member = memberFor(event)

    if (member == null) {
        fail()
    } else {
        val list = BotInitializer.config.moderators
        val memberObj = member.asMember()

        val result = !memberObj.roles.toList().none { list.contains(it.id.value) }

        if (result) {
            pass()
        } else {
            fail(
                "You are not a moderator!"
            )
        }
    }
}