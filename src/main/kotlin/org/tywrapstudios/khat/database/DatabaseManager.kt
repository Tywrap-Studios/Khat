package org.tywrapstudios.khat.database

import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.v1.jdbc.Database
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import org.tywrapstudios.khat.KhatMod
import kotlin.io.path.pathString

const val CONNECTION = "khat.sqlite"

object DatabaseManager {

    private lateinit var database: Database

    fun setup() {
        val dbFilepath = KhatMod.SERVER
            .getWorldPath(LevelResource.ROOT)
            .resolve(CONNECTION).pathString
        val source = SQLiteDataSource(
            SQLiteConfig().apply {
                setJournalMode(SQLiteConfig.JournalMode.WAL)
            }
        ).apply {
            url = "jdbc:sqlite:$dbFilepath"
        }

        database = Database.connect(source)
    }
}