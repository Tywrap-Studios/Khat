# Changelog

### 2.1.0 - Chat service improvements <Badge type="tip" text="Latest"/>
- Add different message types: (see Kamera 0.2.0)
  - TEXT: A regular text message
  - REPLY: A reply to another text message
  - FORWARD: A forwarded message (currently broken, see: [kordlib/kord#1080](https://github.com/kordlib/kord/issues/1080))
  - JUST_EMBED: If a message contains container embeds, it will show the descriptions of them (experimental)
  - OTHER: Any type of message that currently cannot be displayed
- Update dependencies:
  - Exposed: `1.0.0-rc-2` -> `1.6.0`
  - Fabric Loader: `0.18.4` -> `0.19.5`
  - Ktor: `3.3.2` -> `3.6.0`
  - mclogs: `5.0.2` -> `6.2.1`
  - KotlinX RPC: `0.10.1` -> `0.10.4`
  - KordEx: `2.4.1-SNAPSHOT` -> `2.7.0-SNAPSHOT`
  - Krapher: `0.1.1` -> `0.2.0`
  - Kamera: `0.1.0` -> `0.2.0`
- Fix [#2](https://github.com/Tywrap-Studios/Khat/issues/2)
- Fix servers not shutting down properly

#### Kamera 0.2.0 <Badge type="danger" text="Breaking"/>
- Make `ChatService` utilise fully fledged data types, rather than loose parameters. With this, it has been expanded to pass on the following:
  - `MessageData`:
    - author (in the form of `UserData`)
    - message
    - message URL
    - list of people who are pinged in the message (in the form of `UserData`s)
    - type (in the form of `MessageType`)
    - reply user (optional; in the form of `UserData?`)
  - `UserData`
    - name
    - username
    - id
  - `MessageType`
    - TEXT: A regular text message
    - REPLY: A reply to another text message
    - FORWARD: A forwarded message
    - JUST_EMBED: If a message contains container embeds, it will show the descriptions of them
    - OTHER: Any type of message that currently cannot be displayed

#### Krapher 0.2.0
- Update to accommodate Kamera 0.2.0:
  - `ChatExtension` now properly checks what type the message is and passes on the correct data accordingly
  - FORWARD is currently broken, see: [kordlib/kord#1080](https://github.com/kordlib/kord/issues/1080)
  - JUST_EMBED is currently experimental and omits message content (if you send a message with other content and a link, only the link's embed description will be shown)

### 2.0.5 - Minor fixes
- Add support for 26.3

### 2.0.4 - Minor fixes
- Add support for 26.2
- Fix [#1](https://github.com/Tywrap-Studios/Khat/issues/1)
- Make enabling bot without kRPC throw

### 2.0.3 - Updating to 26.1!
- Add support for 26.1, 26.1.1 and 26.1.2
- Update Fabric Loader to 0.18.4

### 2.0.2 Hotfix 2: Electric Boogaloo
- Fixed a bug where too much file watchers get deployed
  because of too many `watchFile` statements to Konf.
  - Webhook configs no longer automatically reload!
- Fixed a bug where player connections were rejected
  due to conflicting Netty versions from the embedded
  Ktor server.
- Updated Krapher to 0.1.1, see below.

#### Krapher 0.1.1
- Fixed a bug where messages in any channel would get broadcast
  to the server as opposed to just watching one channel.

### 2.0.1 Transitive Dependencies Hotfix 
Fixed major bugs, particularly present in the `krpc` and `full` versions, related
to transitive dependencies not being present in the `include` configurations.
This lead to all the functionality not working because the needed classes were
not present during runtime.

### 2.0.0 An Upgrade from CTD <Badge type="tip" text="Major"/>
- Switched codebase to Kotlin
- Switched codebase to utilise Stonecutter Multi-versioning
  - Khat is now available for `1.20, 1.20.1, 1.21.X`
- Khat can now run a local kRPC server, for more information, see [mRPC](./mrpc#the-mrpc-model)
- Khat can now run a local Discord bot, for more information, see [Krapher](./krapher)
- Updated config, for more information, see [its page](../users/config/versions#_3-0)
- We fixed so many bugs, we can't even remember them anymore
- Configs may now be automatically migrated from older versions
- Commands were renamed, and the command logic and code
has been split up for the sake of organisation
- Changed to use our [hookt](https://github.com/Tywrap-Studios/hookt) library, contrary to handling it
fully in this codebase