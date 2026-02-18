# Config Versions and Migration
Older versions of the mod, especially when it was still called
CTD, may have handled config differently. On the other hand, newer
versions might have added or removed certain values, or modified their
behaviour/names. In this page you can view every config version available,
their recommended migration steps, and other notable changes.

## Versions
### 3.0 <Badge type="tip" text="Latest"/> <Badge type="danger" text="Breaking"/>
#### Changelog
- Changed library to Konf
- Changed file type to TOML
- Renamed file: `ctd.json5` -> `global.toml`
- Moved file: `run/config/ctd.json5` -> `run/config/khat/global.toml`
- Introduced per-webhook configs in `run/config/khat/webhooks/`
- **Added values:**
  - `webhook` (Root)
    - `webhook.useComponents` (`Boolean`) (**Experimental**)
  - `rpc` (Root)
    - `rpc.enabled` (`Boolean`)
    - `rpc.port` (`Integer`)
    - `rpc.token` (`String`)
    - `rpc.feature` (Nest)
      - `rpc.feature.chat` (`Boolean`)
      - `rpc.feature.commands` (`Boolean`)
      - `rpc.feature.linking` (`Boolean`)
  - `bot` (Root)
    - `bot.enabled` (`Boolean`)
    - `bot.token` (`String`)
    - `bot.channel` (`String`)
    - `bot.moderators` (`List<String>`)
    - `bot.enforceUsername` (`Boolean`) (**Experimental**)
    - `bot.pattern` (`String`) (**Experimental**)
- **Renamed/Replaced values:**
    - `formatVersion` (`String`) -> `khat.version` (`String`)
    - `discord.webhooks` (`List<String>`) -> `khat.webhooks` (`List<String>`)
    - `discord.embedMode` (`Boolean`) -> `webhook.useEmbeds` (`Boolean`)
    - `discord.embedColorRgbInt` (`Integer`) -> `webhook.primaryColor` (`String`)
    - `discord.roleIds` (`List<String>`) -> `webhook.pingRoles` (`List<String>`)
    - All remaining `discord.*` values were replaced by the per-webhook config
      (-> `webhook.*`)
- **Removed values:**
  - `util` (Root)
    - `util.debug_mode` (`Boolean`)
    - `util.suppress_warns` (`Boolean`)

::: details Defaults
::: code-group
<<< @../../../src/main/resources/default-configs/global.toml
<<< @../../../src/main/resources/default-configs/webhook.toml
:::

#### Migrating <Badge type="tip" text="Auto (2.0→3.0)"/> <Badge type="danger" text="Lost Values"/>
Migrating from 2.0 to 3.0 is done automatically, however, the migration logic can only
read valid JSON code. Comments are automatically removed, but other JSON5
quirks such as trailing commas cannot be taken care of autonomously. You
need to remove them yourself. If you did not explicitly utilise
these quirks, you probably do not need to take action.

Migrating from 1.0 to 3.0 can be done semi-automatically. Follow the steps for
2.0 migration and then let the automatic migration handle the file. You may
need to manually write the 2.0 file. Note: due to 2.0 not being LTS anymore, we do not
provide accurate information on it anymore.

::: danger Lost Values
You will lose the `util` values, as they do not serve a purpose anymore.
:::

### 2.0 <Badge type="danger" text="No LTS"/> <Badge type="danger" text="Breaking"/>
::: danger No LTS
This config version is no longer actively supported, and as such, proper
documentation of it does not exist because we deemed it unfeasible for LTS.
:::
#### Changelog
- Changed library to BlossomBridge API
- Changed file type to JSON5
- Renamed file: `ctd.json` -> `ctd.json5`
- This update most notably added `util` values for logging
and replaced value-based comments with proper JSON5 `//` comments.
#### Migrating
There are currently no automatic actions taken to migrate from
1.0 to 2.0. However, replacing the values is easy.

Get rid of the value-based "comments", they are not needed anymore
and will mess up the parsing. Then, manually put over similar-looking
config values into the newer generated `ctd.json5` file.

### 1.0 <Badge type="danger" text="No LTS"/>
::: danger No LTS
This config version is no longer actively supported, and as such, proper
documentation of it does not exist because we deemed it unfeasible for LTS.
:::
Initial config version.
