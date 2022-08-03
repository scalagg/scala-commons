package net.evilblock.cubed.backup

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import java.util.*

@Service
object BackupHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @JvmStatic
    private val FORMAT =
        SimpleDateFormat("yyyy-dd-MM_hh.mm.ss")

    private val backupFolder by lazy {
        File(
            plugin.dataFolder, "backups"
        )
    }

    @Configure
    fun configure()
    {
        if (!this.backupFolder.exists())
        {
            this.backupFolder.mkdirs()
        }

        val listed = this
            .backupFolder.listFiles()
            ?.toList() ?: return

        if (listed.size < 10)
        {
            return
        }

        val files = listed.sortedBy {
            val creationTime = Files
                .getAttribute(
                    it.toPath(), "creationTime"
                ) as FileTime

            return@sortedBy creationTime
                .toMillis()
        }

        files.forEachIndexed { index, file ->
            if (index > 10)
            {
                file.delete()
            }
        }
    }

    @JvmStatic
    fun findNextBackupFile(id: String): File
    {
        val directory = File(this.backupFolder, id)
            .apply {
                if (!this.exists())
                {
                    this.mkdirs()
                }
            }

        return File(
            directory,
            "${FORMAT.format(Date())}.json"
        )
    }
}
