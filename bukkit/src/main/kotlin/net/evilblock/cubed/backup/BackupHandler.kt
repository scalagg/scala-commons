package net.evilblock.cubed.backup

import net.evilblock.cubed.Cubed
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupHandler {

    private val backupFolder = File(Cubed.instance.dataFolder.parentFile.parentFile, "backups")

    fun initialLoad() {
        backupFolder.mkdirs()
    }

    @JvmStatic
    fun findNextBackupFile(id: String): File {
        return File(backupFolder, "${id}-" + SimpleDateFormat("yyyy-dd-MM_hh.mm.ss").format(Date()) + ".json")
    }

}