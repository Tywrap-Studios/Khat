# Changelog

### 2.0.2 Hotfix 2: Electric Boogaloo <Badge type="tip" text="Latest"/>
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