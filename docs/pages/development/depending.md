# Depending

Currently, Kamera and Krapher are for internal use only. However, we
plan on making the Kamera model more accessible as we go.

If you want to use Kamera in a project, feel free to hit us up in a message
over on our [Discord Server](https://tiazzz.me/discord), or e-mail us at
[business@tywrap-studios.tiazzz.me](mailto:business@tywrap-studios.tiazzz.me). We'd be happy to help and see what we can make possible
for you!

One thing we do want to provide, however, is an exemplary implementation
of a client that interacts with a Kamera server, that we use for
Krapher and testing purposes.

::: info 
The highlighted lines showcase spots where you should
input your own values or configurable values.

This code snippet is taken from Krapher's implementation. It
also contains lines using a `logger`, which you may provide
yourself too, or remove.
:::

::: code-group

<<< @../../../krapher/src/main/kotlin/org/tywrapstudios/krapher/KameraClient.kt{21,34-35}

```kotlin [Example.kt]
suspend fun main() {
    val result = KameraClient.get().withService<CommandService>()
        .run("/kill @e")
    println(result)
    // Killed 56 entities
    
    val online = KameraClient.get().withService<ServerStatsService>()
        .isOnline()
    if (online) {
        println("Server is online!")
    }
    // Server is online!
}
```