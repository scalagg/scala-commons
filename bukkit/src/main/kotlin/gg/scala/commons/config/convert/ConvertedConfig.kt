package gg.scala.commons.config.convert

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.ConfigContainer
import gg.scala.commons.config.ConfigContainerized
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.config.convert.annotations.ConfigCloudSync
import gg.scala.commons.config.convert.annotations.ConfigFileName
import gg.scala.commons.config.convert.annotations.ConfigLateInject
import gg.scala.commons.util.Files
import net.evilblock.cubed.serializers.Serializers
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * @author GrowlyX
 * @since 7/12/2022
 */
class ConvertedConfig(
    plugin: ExtendedScalaPlugin,
    model: KClass<*>,
    containerized: ConfigContainerized
) : ConfigContainer(
    plugin, null,
    ContainerConfig(
        model.java.getAnnotation(ConfigFileName::class.java)!!.value,
        model,
        model.java.getAnnotation(ConfigLateInject::class.java)?.value ?: false,
        model.java.getAnnotation(ConfigCloudSync::class.java)?.value ?: false
    ),
    containerized
)
{
    private val fileName = model.java
        .getAnnotation(ConfigFileName::class.java)!!.value

    private val file = File(
        plugin.dataFolder, fileName
    )

    init
    {
        Serializers.create {
            setPrettyPrinting()
        }
    }

    override fun saveModel(model: Any?)
    {
        if (!file.exists())
            file.createNewFile()

        val json = Serializers
            .gson.toJson(model)

        Files.write(
            json,
            file
        )
    }

    override fun modelByAnnotation(): Any
    {
        val model = annotation.model

        if (!file.exists())
        {
            val instance = model
                .createInstance()

            kotlin.runCatching {
                saveModel(instance)
            }.onFailure {
                it.printStackTrace()
            }

            return instance
        }

        return Serializers.gson
            .fromJson(
                file.readText(), model.java
            )
    }

    override fun fileName() = fileName
}
