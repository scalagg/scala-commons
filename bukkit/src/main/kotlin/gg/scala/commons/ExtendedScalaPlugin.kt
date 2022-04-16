package gg.scala.commons

import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.annotations.Listeners
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizers
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.library.HelperLibraryHandler
import gg.scala.commons.reflections.PackageIndexer
import me.lucko.helper.Schedulers
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.util.CC
import org.apache.commons.lang.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import java.util.logging.Level

/**
 * An extension of [ExtendedJavaPlugin] with support
 * for [HelperLibraryHandler]'s runtime library installations.
 *
 * @author GrowlyX
 * @since 10/15/2021
 */
open class ExtendedScalaPlugin : ExtendedJavaPlugin()
{
    private val packageIndexer by lazy {
        PackageIndexer(this)
    }

    private lateinit var commandManager: CubedCommandManager

    override fun load()
    {
        val java11Enabled = SystemUtils
            .isJavaVersionAtLeast(11)

        if (!java11Enabled)
        {
            logger.info("[Compatability] This version of ${description.name} does not support java ${
                SystemUtils.getJavaVersion()
            }!")
            logger.info("[Compatability] Please use a version higher than 11.")

            Bukkit.shutdown()
            return
        }
    }

    override fun enable()
    {
        this.packageIndexer
            .getTypesAnnotatedWith<LibraryLoader>()
            .mapNotNull {
                it.objectInstance()
            }
            .forEach {
                HelperLibraryHandler
                    .loadAll(it::class.java)
            }

        this.logger.info("Starting container enable process...")

        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerEnable>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure { throwable ->
                    logger.log(Level.WARNING, "Failed to enable container part!", throwable)
                }
            }

        this.commandManager = CubedCommandManager(
            this,
            primary = ChatColor.getByChar(CC.PRI[1]),
            secondary = ChatColor.getByChar(CC.SEC[1])
        )

        for (customizer in CommandManagerCustomizers.customizers)
        {
            customizer.methods.forEach { method ->
                kotlin.runCatching {
                    method.invoke(
                        customizer, this.commandManager
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to customize command manager", it)
                }.onSuccess {
                    logger.info("[Commands] [Default] Customized command manager through ${
                        method.declaringClass.simpleName
                    }#${
                        method.name
                    }().")
                }
            }
        }

        this.packageIndexer
            .getMethodsAnnotatedWith<CommandManagerCustomizer>()
            .forEach { method ->
                kotlin.runCatching {
                    method.invoke(
                        method.declaringClass.objectInstance()!!,
                        this.commandManager
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to customize command manager", it)
                }.onSuccess {
                    logger.info("[Commands] Customized command manager through ${
                        method.declaringClass.simpleName
                    }#${
                        method.name
                    }().")
                }
            }

        this.packageIndexer
            .getTypesAnnotatedWith<AutoRegister>()
            .filter {
                it.isAssignableFrom(ScalaCommand::class.java)
            }
            .forEach {
                kotlin.runCatching {
                    this.commandManager.registerCommand(
                        (it.objectInstance() ?: it.newInstance()) as ScalaCommand
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to register command", it)
                }
            }

        this.packageIndexer
            .getMethodsAnnotatedWith<ManualRegister>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this, this.commandManager)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to manually register command", it)
                }
            }

        this.packageIndexer
            .getTypesAnnotatedWith<Listeners>()
            .mapNotNull {
                it.objectInstance() ?: it.newInstance()
            }
            .forEach {
                this.server.pluginManager
                    .registerEvents(it as Listener, this)
            }

        this.packageIndexer
            .getTypesAnnotatedWith<Repeating>()
            .mapNotNull {
                it.objectInstance() ?: it.newInstance()
            }
            .filterIsInstance<Runnable>()
            .forEach {
                val annotation = it.javaClass
                    .getAnnotation(Repeating::class.java)!!

                Schedulers.get(annotation.context)
                    .runRepeating(
                        it,
                        annotation.delay,
                        annotation.value
                    )
            }
    }

    override fun disable()
    {
        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerDisable>()
            .filter {
                it.declaringClass == this::class.java
            }
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to disable container part!", it)
                }
            }

        this.commandManager.unregisterCommands()
    }

    private fun Class<*>.objectInstance(): Any?
    {
        return kotlin.runCatching {
            getDeclaredField("INSTANCE").get(null)
        }.getOrNull()
    }
}
