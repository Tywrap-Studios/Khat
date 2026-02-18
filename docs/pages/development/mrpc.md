# mRPC
mRPC is an umbrella term for everything related to the Kamera kRPC 
model, the embedded server running an implementation of it, and the 
services related to it. It is meant to be an "upgrade" for RCON, and
works flawlessly with other Kotlin(/Java) applications.

## Backstory
When we were developing [LasCordCrafter](https://github.com/Tywrap-Studios/LasCordCrafter)
, [Chat To Discord's Krafter integration](https://github.com/Tywrap-Studios/chat-to-discord/pull/3)
and [Krafter Standalone](https://docs.tiazzz.me/Krafter)
, we kept being hit with the fact we had to use RCON
to communicate with the server. This is a bummer because, while RCON is nice
and simple, it does not fulfill a lot of our advanced needs, on top of being
bad software in general.
- It's outdated, it comes from the old Source Engine implementation of this
protocol.
- It's not encrypted and sent over plain text (not even HTTP!), which makes
it incredibly prone to MitM attacks.
- It isn't very consistent and ordered when it comes to returning a response
from the server:
  - Imposed packet limits, which means clients either need to completely
  reject long responses, or await them which could potentially take long.
  - Responses are packets, not status codes.
  - Sometimes you need more information than just the command response.

And on the other side: it only provides command functionality.
We are aware that this is the point, RCON, after all, stands for 
"**R**emote **Con**sole". However, for our use cases, such as sending
messages to the server, this can become a pain to work with. A good example is that
in the past, we had to use a monster of a  `/tellraw` command just to send a 
styled bit of text to the server from Discord.

Because of all these reasons, we wanted to make our own "protocol", or at 
least a piece of software that was "better" than RCON. When Minecraft released
an update introducing JSON-RPC server management, we thought of implementing
similar logic, based on JSON-RPC, but for for instance chatting. However, after
some research, we also found that KotlinX provides an RPC protocol called
Kotlin-RPC (kRPC for short). It integrates nicely with Kotlin and Ktor based
projects and allows you to make remote procedure calls (RPCs) straight from
the code, without the developer needing to do any serialisation logic
themselves, like with JSON-RPC. And it does this as if you're directly
calling the code in the server, to the user and to the developer, the
client medium is almost not exposed.

## The mRPC model
::: tip ‼️Are you a user? Here's a TL;DR/easier-to-understand explanation!
RCON sucks, so we made a better version! It uses kRPC, which stands
for Kotlin Remote Procedure Call. It's like RCON, but not a butchered
protocol and allows for more than just commands, like easier cross-platform
chatting, and linking your accounts. Any external app can use it, it does not
have to be a Discord bot. It does this through the use of "services" and
running them on an internal server in the Minecraft server. Don't worry though,
it isn't very intensive, and you won't notice a performance drop.
:::
::: warning Are you a dev or interested? Heads up!
This part of the documentation assumes you know a bit of basic
(JVM-based languages) coding knowledge. However, it is not
fully required.
:::
kRPC works based on a model defined with `@Rpc` annotations on interfaces.
Those interfaces can then in turn contain suspended functions and/or
functions returning a `Flow`. Currently, mRPC only uses the former.
These interfaces are also known as services.

A server can implement this model and expose their service implementation
to clients by running an embedded (Ktor) server with those services, and
that is exactly what Khat does. A client then on its turn only has to depend
on the model, and instead of providing implementations itself, it can make
requests using a (Ktor) client with those services, essentially
casting the client into that service allowing you to use the methods directly.
The (Ktor) client then automatically converses with the server to properly
handle the RPC.

Kamera has separate versioning from Khat and currently governs the
following services:

### ChatService <Badge type="tip" text="^0.1.0"/>
This service should allow external clients to send chat messages to
the server as a person with a name, username and user ID.

::: tip Khat Implementation
The name is displayed in between `<>` as a blue text, when hovered
over this blue text, the username is shown, and when that text is
clicked it pastes a string of text into your chat that mentions
the user involved (utilising the user's ID).

Then follows the chat message as a grey piece of text and upon clicking this,
you copy the content to your clipboard, indicated by a hover text too.
:::

### CommandService <Badge type="tip" text="^0.1.0"/>
This service is similar to the RCON functionality. It simply
should allow clients to run any command at a dev-chosen permission level
and return the outputs of the command. If the output of the command
is empty or there is no output, that must be clarified in the returned string.
At no point should the service intentionally return an empty output.

::: tip Khat Implementation
Khat implements the `CommandSource` interface with an implementation
similar to that of the RCON source. Just a little cleaner and always
informing operators of the applied action by sharing the feedback.

When a command is run, the responses are built up into a buffered string,
when it finishes, the string gets returned. If the string is empty we
return "No or empty response". We directly run the command from the
dedicated server's Commands manager on permission level 4.
:::

### LinkService <Badge type="tip" text="^0.1.0"/>
This service should let users from a platform with IDs based on
Unsigned Longs (Snowflakes) link that ULong to a UUID on the platform
the player plays on. Most often these are Discord Snowflakes mapped to
Minecraft UUIDs. It allows users to link, unlink and view their link statuses,
as well as allow moderators to forcefully link users and view their statuses
as well. The link code must be somewhat secure and code expiration must be applied
in order to further secure the linking process.

::: tip Khat Implementation
Khat implements basically everything said above. It stores everything
in a local SQLite database, on a per-world basis.

The codes are generated using `SecureRandom` and are 16-character
long alphanumeric codes.
:::

### ServerStatsService <Badge type="tip" text="^0.1.0"/>
This service should give the client insight on the server's status.
The server status includes: whether it is online or not, what the
current amount of players online is and what the maximum amount
of players online is. If the embedded kRPC server runs alongside
the platform server, it is obvious that reporting if it's
online or not is impossible. Therefore, clients may assume
that an error from this call indicates the server is offline
or in an exceptional state.

::: tip Khat Implementation
It simply reroutes the calls to `MinecraftServer` calls:
- `isOnline()` -> `SERVER.isRunning`
- `playerCount()` -> `SERVER.playerCount`
- `maximumPlayers()` -> `SERVER.maxPlayers`
:::