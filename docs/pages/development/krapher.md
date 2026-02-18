# Krapher

Krapher is the built-in bot software built on top of the
[KordEx](https://kordex.dev) framework. It strives to be
the most minimal-setup bot out there, while still providing
some neat extras.

## Available Functions
KordEx works on an "Extensions" principle. Every Extension
you add to your bot can have different functionalities, and
we made and add our functionalities based on the [mRPC config](/users/config/global#rpc-feature).

Krapher has separate versioning from Khat and currently governs the
following extensions:

### ChatExtension <Badge type="tip" text="^0.1.0"/>
Watches the chat specified in the config for new messages from
members, and sends them to the server's `ChatService`. If
your account is linked, instead of using your Discord
display name, it uses your Minecraft username.

### CommandExtension <Badge type="tip" text="^0.1.0"/>
Adds the `/cmd` command group containing `/cmd run` and
`/cmd list`. The `run` command allows moderators to run
any command to your server with **level 4** access,
similar to operators. `/cmd list` is a publicly available
command that anyone can use, and simply returns the response
of the `/list` command.

### LinkingExtension <Badge type="tip" text="^0.1.0"/>
Makes users able to link their profiles via the
`LinkService`. It provides basic slash commands for starting
a link, unlinking and forcefully linking players. 

After starting a link, you will get a code and a command
you can run in-game to verify yourself. This must be done
within 15 minutes of starting your linking process.

::: info NOTE
Viewing links is handled by a different extension.
:::

### LookupExtension <Badge type="tip" text="^0.1.0"/>
Allows users and moderators to view information about
Minecraft accounts and look them up. If an account is
linked to a user, it displays who owns the account and
whether they verified that link or not. You can also search
based on members directly, and once again, if they are linked,
you can view their link status.

### MiscExtension <Badge type="tip" text="^0.1.0"/> <Badge type="tip" text="Contains Experimentals"/>
This extension mostly contain functionality
that do not provide a single, clear purpose that
would need an entire new extension.

This includes:
- Updating the bot's status as per the server's status.
- Adding commands and functionality to enforce usernames.