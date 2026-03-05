<div align="center">
  <h1>Khat</h1>

[![fabric-api](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api)
[![fabric-language-kotlin](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-language-kotlin_vector.svg)](https://modrinth.com/mod/fabric-language-kotlin)

[![discord-plural](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/social/discord-plural_vector.svg)](https://tiazzz.me/discord)
[![ghpages](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/documentation/ghpages_vector.svg)](https://docs.tiazzz.me/Khat)

**Chat To Discord, Discord To Chat**
</div>

## About

Khat is a Minecraft mod that creates a bridge between your 
Discord and Minecraft server. You give it a webhook and a 
bot token, and it can create a seamless experience for you
and your players.

It contains several built-in features:
- Sending Player Messages.
- Sending Game Messages (e.g. Deaths, Advancements).
- Sending Command Messages (e.g. /say).
- Sending a Start and Stop message for the respective server lifecycle events.
- Sending Discord messages straight back to the server.
- Letting you link your profile with Minecraft.
- And also, before your message is sent, it goes through some necessary Compatibility handlers, which most mods lack:
  - The message is modified to negate global pings (e.g. `@everyone`).
  - The message is modified to negate invite links.
  - The message is modified to negate any role pings that you don't allow in Config.
  - The message and player names in it are modified to bypass Discord's native Markdown features (such as _Cursive text_).
  - When sharing a waypoint using Xaero's Mini-/Worldmap, it displays a very "weird" message, this message can actually be deciphered using some logic, and this mod rewrites your message to be more legible.
- If the server crashes, a specialised embed will be sent, displaying the main cause, and also linking to an automatically made Mclogs Site!

And so much more! Check out all the available functions by checking the docs, or trying them out yourself.

Furthermore, you can also download versions of the mod _without_ certain major functionalities, like the bot mode. A good example of this is only including the kRPC server, and having your own custom bot interact with your server!

## Installing  
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/chat-to-discord/versions)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/Tywrap-Studios/Khat/releases)
