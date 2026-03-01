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