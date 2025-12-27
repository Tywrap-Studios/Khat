package org.tywrapstudios.khat.compat

import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.config.getGlobalConfig
import org.tywrapstudios.khat.config.KhatSpec
import java.util.regex.Matcher;
import java.util.regex.Pattern;

fun String.modifyToNegateDangerousPings(): String {
    var message = this
    message = message.replace("@everyone", "`@everyone`[ping negated]")
    message = message.replace("@here", "`@here`[ping negated]")
    message = message.modifyForRoleMentions()
    return message
}

fun String?.modifyForRoleMentions(): String {
    val allowedRoles: MutableList<String> = getGlobalConfig()[KhatSpec.DiscordSpec.roles]
    val pattern: Pattern = Pattern.compile("<@&(\\d+)>")
    val matcher: Matcher = pattern.matcher(this)

    val modifiedMessage = StringBuilder()

    while (matcher.find()) {
        val roleId: String? = matcher.group(1)

        if (allowedRoles.contains(roleId)) {
            matcher.appendReplacement(modifiedMessage, "<@&$roleId>")
        } else {
            matcher.appendReplacement(modifiedMessage, "`$roleId`[ping negated]")
        }
    }

    matcher.appendTail(modifiedMessage)
    return modifiedMessage.toString()
}

fun String.modifyToNegateInviteLinks(): String = this.replace(
    "(https?://(discord\\.gg|discord\\.com/invite)/[a-zA-Z0-9-]+)".toRegex(),
    "[Discord Invite]"
)

fun String.modifyToNegateMarkdown(): String = this.replace("_", "\\_")