package net.evilblock.cubed.scoreboard.preview

import com.google.common.base.Charsets
import com.google.gson.reflect.TypeToken
import gg.scala.commons.util.Files
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import java.io.File
import java.util.*

@Service
object ScoreboardPreviewHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val dataType = object : TypeToken<List<String>>()
    {}.type

    private val dataFile by lazy {
        File(plugin.dataFolder, "scoreboard-preview.json")
    }

    var lines: MutableList<String> = arrayListOf()

    internal var previewGlobal: Boolean = false
    internal var previewMode: MutableSet<UUID> = hashSetOf()

    @Configure
    fun configure()
    {
        if (dataFile.exists())
        {
            Files.read(dataFile) { reader ->
                try
                {
                    lines = Serializers.gson.fromJson(reader, dataType) as MutableList<String>
                } catch (e: Exception)
                {
                    plugin.logger.severe("Failed to parse scoreboard-preview.json!")
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveData()
    {
        try
        {
            Files.write(Serializers.gson.toJson(lines, dataType), dataFile)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

}
