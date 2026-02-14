package org.tywrapstudios.khat.logic

import com.uchuhimo.konf.Config
import org.tywrapstudios.khat.compat.convertWayPointMessage
import org.tywrapstudios.khat.compat.modifyToNegateDangerousPings
import org.tywrapstudios.khat.compat.modifyToNegateInviteLinks
import org.tywrapstudios.khat.compat.modifyToNegateMarkdown

fun String.handleAll(config: Config): String {
    var message = this
    message = message.convertWayPointMessage()
    message = message.modifyToNegateDangerousPings(config)
    message = message.modifyToNegateInviteLinks()
    message = message.modifyToNegateMarkdown()

    return message
}