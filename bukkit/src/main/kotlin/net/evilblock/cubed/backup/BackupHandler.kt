package net.evilblock.cubed.backup

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Service
object BackupHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val backupFolder by lazy {
        File(
            plugin.dataFolder.parentFile.parentFile,
            "backups"
        )
    }

    @Configure
    fun configure()
    {
        backupFolder.mkdirs()
    }

    @JvmStatic
    fun findNextBackupFile(id: String): File
    {
        return File(backupFolder, "${id}-" + SimpleDateFormat("yyyy-dd-MM_hh.mm.ss").format(Date()) + ".json")
    }
}
