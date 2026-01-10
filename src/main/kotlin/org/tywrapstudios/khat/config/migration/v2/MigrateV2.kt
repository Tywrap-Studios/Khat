package org.tywrapstudios.khat.config.migration.v2

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml.toToml
import net.fabricmc.loader.api.FabricLoader
import org.tywrapstudios.khat.config.CONFIG_PATH
import org.tywrapstudios.khat.config.GLOBAL_PATH
import org.tywrapstudios.khat.config.KhatSpec
import org.tywrapstudios.khat.config.WebhookSpec
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.webhooks
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readText
import kotlin.io.path.writer
import kotlin.time.Clock

val V2_PATH: Path = FabricLoader.getInstance()
    .configDir
    .resolve("ctd.json5")

object MigrateV2 {
    fun attemptMigrate() {
        if (!Files.exists(V2_PATH)) {
            return
        }
        val file = V2_PATH.toFile()
        val migrationPath = FabricLoader.getInstance()
            .configDir
            .resolve(CONFIG_PATH)
            .resolve(".migrate/v2/${file.hashCode().toHexString()}.json")
        migrationPath.parent.createDirectories()
        if (!Files.exists(migrationPath)) {
            Files.copy(
                V2_PATH,
                migrationPath,
            )
        }

        val config = Config {
            addSpec(V2Spec.DiscordSpec)

            println(migrationPath
                .readText()
                .replace("// .*$|/\\*[\\s\\S]*?\\*/".toRegex(RegexOption.MULTILINE), "")
            )
        }
            .from.json.bytes(migrationPath
                .readText()
                .replace("// .*$|/\\*[\\s\\S]*?\\*/".toRegex(RegexOption.MULTILINE), "")
                .toByteArray()
            )

        val globalPath = FabricLoader.getInstance().configDir.resolve(GLOBAL_PATH)
        if (!Files.exists(globalPath) || !globalConfig.containsRequired()) {
            throw IllegalStateException("The global config file does not yet exist or contain all the values: $globalPath")
        }

        globalConfig[KhatSpec.webhooks] = config[V2Spec.DiscordSpec.webhooks]
        globalConfig.toToml.toFile(globalPath.toFile())

        webhooks.forEach {
            it.config[WebhookSpec.onlyMessages] = config[V2Spec.DiscordSpec.onlyMessages]
            it.config[WebhookSpec.useEmbeds] = config[V2Spec.DiscordSpec.embedMode]
            it.config[WebhookSpec.primaryColor] = "#${config[V2Spec.DiscordSpec.embedColorRgbInt].toHexString()}"
            it.config[WebhookSpec.pingRoles] = config[V2Spec.DiscordSpec.roleIds]
            it.config.toToml.toFile(it.generateFile())
        }

        val readmePath = FabricLoader.getInstance()
            .configDir
            .resolve(CONFIG_PATH)
            .resolve("README.md")
        readmePath.deleteIfExists()
        readmePath.createFile()
        readmePath.writer().use {
            it.append("# Migrations")
            it.appendLine()
            it.append("Your configs have been migrated from 2.0 to 3.0.")
            it.appendLine()
            it.appendLine()
            it.append("""This means the following:
                |- Your new 3.0 config files contain values from your previous setup;
                |- Due to a technical limitation, your config files no longer contain helpful
                |comments (you can go to 
                |https://github.com/Tywrap-Studios/Khat/tree/main/src/main/resources/default-configs
                |in case you want to view them);
                |- Your old config file has been moved to `${migrationPath.toAbsolutePath()}` in case you want to
                |access it.
            """.trimMargin())
            it.appendLine()
            it.appendLine()
            it.append("${Clock.System.now()}")
        }

        file.delete()
    }
}