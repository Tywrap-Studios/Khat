# Global Configuration

The global config file can be found at `run/config/khat/global.toml`.
The file is a TOML file, and if your config wasn't migrated, should
contain informational comments in order to guide you with simple
explanations. 

## Values
### [khat]
Values under the `[khat]` spec are for the basic functionality
of the mod.

::: info `version`
Type: `String`  
Default: `"3.0"`  
Example:
```toml
version = "3.0"
```
This is an internal value used to determine whether your config is valid
for the current instance of the mod. Automatic migrations might be made in order
to keep you updated, so do NOT manually change this unless you know what you're
doing! Otherwise, your values may be lost.
:::
::: info `webhooks`
Type: `List` of `Strings`  
Default: _Empty List_  
Example:
```toml
webhooks = [
    "https://discord.com/api/webhooks/1284.../Ewah...",
    "https://discord.com/api/webhooks/2374.../wbHJ..."
]
```
A list with strings with webhook endpoint URLs. See this 
[Discord support article](https://tywrap-studios.tiazzz.me/r/webhooks)
on how to create one. Paste the full link from "Copy Webhook URL".
:::

### [rpc] <Badge type="warning" text="Requires krpc or full" />
Values under the `[rpc]` spec are for the mRPC server.
More information about mRPC can be found [here](/development/mrpc#the-mrpc-model).

::: info `enabled`
Type: `Boolean`  
Default: `false`  
Example: 
```toml
enabled = true
```
Whether to start an embedded kRPC server for incoming connections.
An example of an incoming connection is a Discord bot attempting to
send commands, messages or linking logic over to the server.
More information about kRPC can be found on the [mRPC page](/development/mrpc#the-mrpc-model).
:::
::: info `port`
Type: `Integer`  
Default: `34230`  
Example:
```toml
port = 34230
```
This is the port that the embedded RPC server runs on.
When running a local bot, this does not need to be
exposed. However, if you want to enable external
applications to utilise your RPC server, you need to
expose it.
:::
::: info `token`
Type: `String`  
Default: _Empty String_  
Example:
```toml
token = "SecurePassword1234"
```
This is the token (password) to access the RPC server.  
::: danger
Keep it safe and secure, it has similar command
access like RCON when enabled and has other specific types of
access to other functions that may be dangerous if exposed
to malicious users.
:::

### [rpc feature] <Badge type="warning" text="Requires krpc or full" />
Values under the `[rpc.feature]` spec are for the actual functionality
the mRPC server exposes. For more information on the services,
click on them.

::: info `chat`
Type: `Boolean`  
Default: `true`  
Example:
```toml
chat = true
```
Whether to enable the [`ChatService`](/development/mrpc#chatservice).
:::
::: info `commands`
Type: `Boolean`  
Default: `false`  
Example:
```toml
commands = true
```
Whether to enable the [`CommandService`](/development/mrpc#commandservice).
::: warning
The mRPC server has **level 4** access to your server. In some
cases this can be dangerous.
:::
::: info `linking`
Type: `Boolean`  
Default: `false`  
Example:
```toml
linking = true
```
Whether to enable the [`LinkService`](/development/mrpc#linkservice).
:::

### [bot] <Badge type="warning" text="Requires full" />
Values under the `[bot]` spec are for the integrated bot.

::: info `enabled`
Type: `Boolean`  
Default: `false`  
Example: 
```toml
enabled = true
```
Whether to enable the bot to run while the server is active.

It requires a running RPC server, and will not start if the RPC
server is not enabled. Furthermore, available features can
be configured using the RPC config, because it does not provide
more functionality than the RPC server can send back.

The bot runs on the [KordEx](https://kordex.dev) software, you may see a log message about
data collection, however, we disabled data collection. Data collection
cannot be enabled.
:::
::: info `token`
Type: `String`  
Default: _Empty String_  
Example:
```toml
token = "Yu3AyHha7JiElT6...."
```
The token of the bot, from the developer dashboard. This mod cannot
automatically make a bot token for you, and hence you need to provide
your own application and token. 

How to create and get a token: https://tywrap-studios.tiazzz.me/r/bot-tokens
::: danger
Similar to the RPC token, keep this safe and secure. Contrary to your
RPC server though, this token can be even more dangerous if exposed
to malicious users, especially if you gave your bot Administrator
permissions.
:::
::: info `channel`
Type: `String`  
Default: _Empty String_  
Example:
```toml
channel = "1256813113421249424"
```
The **ID** of the channel to watch for messages to send. 

::: info NOTE
This is the ID of the channel, not the name.
:::
::: info `moderators`
Type: `List` of `Strings`  
Default: _Empty List_  
Example:
```toml
moderators = [
    "1248641236475625535",
    "1236475625253548646"
]
```
A list of **ID**(s) of role(s) that have moderator permissions
in your Discord server. What this means is that they can use
privileged commands such as `/cmd run` to run any command on the
server or to forcefully link accounts.
::: warning
The mRPC server has **level 4** access to your server. In some
cases this can be dangerous permissions to give to your
moderators.
:::
::: info <Badge type="tip" text="Experimental" /> <p>`enforceUsername`</p>
Type: `Boolean`  
Default: `false`  
Example:
```toml
enforceUsername = true
```
Whether to enforce a username pattern for linked accounts. If a Discord
member linked their account to Minecraft, you can enforce their Discord
display name to follow a strict pattern.
:::
::: info <Badge type="tip" text="Experimental" /> <p>`pattern`</p>
Type: `String`  
Default: `"$displayName ($minecraft)"`  
Example:
```toml
pattern = "$minecraft"
```
The strict pattern `enforceUsername` follows. This pattern is applied
every time a user updates their profile or when a manual update is
requested using `/misc update-profiles`.

##### Replacements:
`$displayName` -> the user's **global** username (note: this is not the user's
member display name in the server, this is overridden)  
`$minecraft` -> the user's linked Minecraft username

> [!IMPORTANT]
> While you can define any pattern however you want, keep in mind that Discord has
a display name character **cap of 32 characters**.
:::
