# Per-webhook Configuration

Every webhook added in [`global.toml`](./global)'s `webhooks` value gets a designated
config file. They can be found at `run/config/khat/webhooks/<id>.toml`.
The file is a TOML file, and if your config wasn't migrated, should
contain informational comments in order to guide you with simple
explanations. 

## Values
### [webhook]
::: info `onlyMessages`
Type: `Boolean`  
Default: `false`  
Example: 
```toml
onlyMessages = false
```
Whether to only send chat messages sent by players, and not messages
such as joins, leaves and deaths.
:::
::: info `useEmbeds`
Type: `Boolean`  
Default: `false`  
Example: 
```toml
useEmbeds = true
```
Whether to use rich embeds instead of plain content for the messages.
This is generally bigger and clunkier, but displays the player's head
as an icon.
:::
:::  info <Badge type="tip" text="Experimental" /> <p>`useComponents`</p>
Type: `Boolean`  
Default: `false`  
Example:
```toml
useComponents = true
```
Whether to use Discord's Components V2 instead of plain content for the messages.
Can only be used if `useEmbeds` is set to `false`. Visually, this does not change
much for most of the mod. However, things like crash messages look prettier
and less clunky due to the fact they do no longer use embeds.

::: tip
If you're not using embeds anyway, we suggest enabling this instead! While
it is off by default due to it being relatively new (at the time of
writing), turning it on does almost no harm.
:::
::: info `primaryColor`
Type: `String` HEX Code  
Default: `"#A2EDFF"`
<img height="20" src="/blue_example.png" title="Light Blue-ish Coloured Square" width="20"></img>
Example:
```toml
primaryColor = "#FF9585"
```
<img height="20" src="/pink_example.png" title="Pink-ish Coloured Square" width="20"></img>
The primary colour, as a HEX value, to use throughout the mod. For instance, for
embeds.
:::
::: info `pingRoles`
Type: `List` of `Strings`  
Default: _Empty List_  
Example:
```toml
pingRoles = [
    "1248646236475625535"
]
```
A list with strings with role **ID**s that users are allowed to ping from the
server. `@everyone` and `@here` is negated by default. The reason this is an
available config value is that otherwise the webhook would be able to ping
any role, including roles that every member would have, like `@Member` or
`@Verified`.

