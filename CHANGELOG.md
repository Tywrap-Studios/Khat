### 2.1.0 - Chat service improvements
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

#### Kamera 0.2.0
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