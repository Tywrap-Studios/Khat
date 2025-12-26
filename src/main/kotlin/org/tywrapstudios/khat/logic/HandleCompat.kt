package org.tywrapstudios.khat.logic

import org.tywrapstudios.khat.compat.*

fun String.handleAll(): String {
    var message = this
    message = message.convertWayPointMessage()
    message = message.modifyToNegateDangerousPings()
    message = message.modifyToNegateInviteLinks()
    message = message.modifyToNegateMarkdown()

    return message
}