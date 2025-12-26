package org.tywrapstudios.khat.compat

fun String.convertWayPointMessage(): String {
    if (!this.startsWith("xaero-waypoint")) {
        return this
    }
    val parts = this.split(":")

    // xaero-waypoint : Shack : X : -906 : 64 : -2790 : 12 : false : 0 : Internal-overworld-waypoints
    //          0         1     2     3     4     5      6    7      8       9
    val name = parts[1]
    val x = parts[3]
    val y = parts[4]
    val z = parts[5]
    val dimension = getDimension(parts[9])

    return "Shared Waypoint \"$name\" with Coordinates $x $y $z in $dimension."
}

private fun getDimension(part: String): String {
    val parts = part.split("-")
    return parts[parts.size - 1]
}