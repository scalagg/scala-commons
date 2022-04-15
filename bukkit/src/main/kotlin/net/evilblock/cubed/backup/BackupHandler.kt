package net.evilblock.cubed.backup

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.Cubed
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Service
object BackupHandler
{
    private val backupFolder = File(
        Cubed.instance.dataFolder.parentFile.parentFile,
        "backups"
    )

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
