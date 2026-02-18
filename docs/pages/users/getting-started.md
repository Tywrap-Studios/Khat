# Getting Started

## Prerequisites
Before you can run the Khat mod in Minecraft, ensure you have the following:
- A dedicated server, Khat does not run on the client;
- A compatible Minecraft version (see 
[official releases](https://modrinth.com/mod/chat-to-discord/versions));
- Fabric API and Fabric Language Kotlin.

## Installing
### Choosing a mod version
Khat is released for a multitude of Minecraft versions and the Fabric loader,
however, it's also separated into three main functionalities:
- Basic Webhook functionality. (base)
- Ditto + a built-in mRpc server. (krpc)
- Ditto + a built-in bot. (full)

Most users will want to use the latter version, however, if you want to use a different
one you can download it from the same mod host version page. Instead of downloading
the main file (`ctd-<version>+<mc-version>-full.jar`), download the following file(s):

- Basic Webhooks -> `ctd-<version>+<mc-version>.jar`
- Ditto + mRpc -> `ctd-<version>+<mc-version>-krpc.jar`

### Running
After you've retrieved the jars you want, you simply put it in your `mods`
folder, next to Fabric API and Fabric Language Kotlin, and run your server.

:::info Configuration
The mod creates new configuration files on the first run (or migrates
them from a previous version if you're updating the mod. See: [Config Migration](./config/versions#migrating)),
it is suggested to close down your server after running it for the first time
to configure your instance.
:::