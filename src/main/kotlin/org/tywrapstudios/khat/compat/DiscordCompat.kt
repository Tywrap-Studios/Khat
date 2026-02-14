package org.tywrapstudios.khat.compat

import com.uchuhimo.konf.Config
import org.tywrapstudios.khat.config.WebhookSpec
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.modifyToNegateDangerousPings(config: Config): String {
    var message = this
    message = message.replace("@everyone", "`@everyone`[ping negated]")
    message = message.replace("@here", "`@here`[ping negated]")
    message = message.modifyForRoleMentions(config)
    return message
}

fun String?.modifyForRoleMentions(config: Config): String {
    val allowedRoles: MutableList<String> = config[WebhookSpec.pingRoles]
    val pattern: Pattern = Pattern.compile("<@&(\\d+)>")
    val matcher: Matcher = pattern.matcher(this as CharSequence)

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