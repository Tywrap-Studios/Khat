# Changelog
### 2.0.0 <Badge type="tip" text="Latest"/>
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