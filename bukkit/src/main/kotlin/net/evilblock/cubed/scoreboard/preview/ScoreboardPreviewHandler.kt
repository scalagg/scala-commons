package net.evilblock.cubed.scoreboard.preview

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.serializers.Serializers
import java.io.File
import java.util.*

@Service
object ScoreboardPreviewHandler
{
    private val dataType = object : TypeToken<List<String>>()
    {}.type

    private val dataFile = File(Cubed.instance.dataFolder, "scoreboard-preview.json")

    var lines: MutableList<String> = arrayListOf()

    internal var previewGlobal: Boolean = false
    internal var previewMode: MutableSet<UUID> = hashSetOf()

    @Configure
    fun configure()
    {
        if (dataFile.exists())
        {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                try
                {
                    lines = Serializers.gson.fromJson(reader, dataType) as MutableList<String>
                } catch (e: Exception)
                {
                    Cubed.instance.logger.severe("Failed to parse scoreboard-preview.json!")
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveData()
    {
        try
        {
            Files.write(Serializers.gson.toJson(lines, dataType), dataFile, Charsets.UTF_8)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

}
