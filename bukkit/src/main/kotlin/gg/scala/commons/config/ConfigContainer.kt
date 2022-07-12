package gg.scala.commons.config

import gg.scala.aware.message.AwareMessage
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.util.Players
import gg.scala.flavor.FlavorBinder
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import xyz.mkotb.configapi.ConfigFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
open class ConfigContainer(
    val plugin: ExtendedScalaPlugin,
    private val factory: ConfigFactory?,
    val annotation: ContainerConfig,
    private val containerized: ConfigContainerized
) : Runnable
{
    var model: Any? = null

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getModelAs(kClass: KClass<T>): T
    {
        return model as T
    }

    inline fun <reified T> getModelAs(): T
    {
        return model as T
    }

    private var injected = false

    private val configPath: Path by lazy {
        val dataContainer =
            plugin.dataFolder

        File(
            dataContainer,
            fileName()
        ).toPath()
    }

    private var lastModificationTimestamp = 0L

    override fun run()
    {
        kotlin.runCatching {
            val fileAttributes = Files
                .readAttributes(
                    this.configPath,
                    BasicFileAttributes::class.java
                )

            val lastModified =
                fileAttributes
                    .lastModifiedTime()
                    .toMillis()

            if (this.lastModificationTimestamp == 0L)
            {
                this.lastModificationTimestamp = lastModified
                return
            }

            if (lastModified != this.lastModificationTimestamp)
            {
                this.plugin.logger.info(
                    "[Config] [Watcher] Detected config modification to ${
                        annotation.value
                    } injected into ${
                        annotation.model.simpleName
                    }, reloading model!"
                )

                Players.operators().forEach {
                    it.sendMessage("${CC.GREEN}[Config Watcher] ${CC.YELLOW}Detected changes to ${CC.D_AQUA}${fileName()}{CC.YELLOW} bound to plugin ${CC.GOLD}${plugin.name}${CC.YELLOW}.")
                }

                this.load()
            }

            this.lastModificationTimestamp = lastModified
        }
    }

    fun postInject()
    {
        if (injected)
            return

        if (this.annotation.crossSync)
        {
            this.containerized.aware.listen(
                this.annotation.model.java.simpleName
            ) {
                val content = Serializers.gson
                    .fromJson(
                        this.retrieve(
                            "config", String::class
                        ),
                        annotation.model.java
                    )

                model = content
                save(crossSync = false)

                plugin.logger.info(
                    "[Config] [Sync] Synchronized config ${
                        annotation.value
                    } injected into ${
                        annotation.model.simpleName
                    }."
                )
            }

            plugin.logger.info(
                "[Config] [Sync] Listening to incoming requests for config ${
                    annotation.value
                } injected into ${
                    annotation.model.simpleName
                }."
            )
        }

        ConfigExecutor
            .scheduleAtFixedRate(
                this, 500L,
                500L, TimeUnit.MILLISECONDS
            )

        this.injected = true
    }

    fun save(
        crossSync: Boolean = true
    )
    {
        if (this.model == null)
        {
            throw IllegalStateException("Model has not been loaded yet")
        }

        kotlin.runCatching {
            saveModel(model)

            if (crossSync && this.annotation.crossSync)
            {
                AwareMessage.of(
                    this.annotation.model.java.simpleName,
                    this.containerized.aware,
                    "config" to Serializers
                        .gson.toJson(this.model!!)
                ).publish()
            }
        }.onFailure {
            this.plugin.logger.log(
                Level.WARNING, "[Config] Failed to load config by name \"${annotation.value}\"!", it
            )
        }.onSuccess {
            this.plugin.logger.info(
                "[Config] Saved config ${
                    annotation.value
                } injected into ${
                    annotation.model.simpleName
                }."
            )
        }
    }

    fun load()
    {
        kotlin.runCatching {
            this.model = modelByAnnotation()
        }.onSuccess {
            this.plugin.logger.info(
                "[Config] Loaded config ${
                    annotation.value
                } injected into ${
                    annotation.model.simpleName
                }."
            )

            plugin.flavor().binders.add(
                FlavorBinder(annotation.model) to getModelAs()
            )
        }.onFailure {
            this.plugin.logger.log(
                Level.WARNING, "[Config] Failed to load config by name \"${annotation.value}\"!", it
            )
        }
    }

    open fun modelByAnnotation(): Any
    {
        return factory!!.fromFile(
            annotation.value, annotation.model.java
        )
    }

    open fun saveModel(model: Any?)
    {
        this.factory!!.save(annotation.value, model)
    }

    open fun fileName() = "${this.annotation.value}.yml"
}
