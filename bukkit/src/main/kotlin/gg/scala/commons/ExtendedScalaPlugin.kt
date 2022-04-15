package gg.scala.commons

import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.annotations.Listeners
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizers
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.library.HelperLibraryHandler
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.util.CC
import org.apache.commons.lang.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
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
    private val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(
                this::class.java.`package`.name
            )
    )

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

        this.reflections
            .getTypesAnnotatedWith(LibraryLoader::class.java)
            .mapNotNull {
                it.kotlin.objectInstance
            }
            .forEach {
                HelperLibraryHandler
                    .loadAll(it::class.java)
            }
    }

    override fun enable()
    {
        this.reflections
            .getMethodsAnnotatedWith(ContainerEnable::class.java)
            .filter {
                it.declaringClass == this::class.java
            }
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to enable container part!", it)
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

        this.reflections
            .getMethodsAnnotatedWith(CommandManagerCustomizer::class.java)
            .filter {
                it.parameterCount == 1 && it.declaringClass.kotlin.objectInstance != null
            }
            .forEach { method ->
                kotlin.runCatching {
                    method.invoke(
                        method.declaringClass.kotlin.objectInstance!!,
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

        this.reflections
            .getTypesAnnotatedWith(AutoRegister::class.java)
            .filter {
                it.isAssignableFrom(ScalaCommand::class.java)
            }
            .forEach {
                kotlin.runCatching {
                    this.commandManager.registerCommand(
                        (it.kotlin.objectInstance ?: it.newInstance()) as ScalaCommand
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to register command", it)
                }
            }

        this.reflections
            .getMethodsAnnotatedWith(ManualRegister::class.java)
            .filter {
                it.parameterCount == 1
            }
            .forEach {
                kotlin.runCatching {
                    it.invoke(this, this.commandManager)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to manually register command", it)
                }
            }

        this.reflections
            .getTypesAnnotatedWith(Listeners::class.java)
            .mapNotNull {
                it.kotlin.objectInstance ?: it.newInstance()
            }
            .forEach {
                this.server.pluginManager
                    .registerEvents(it as Listener, this)
            }
    }

    override fun disable()
    {
        this.reflections
            .getMethodsAnnotatedWith(ContainerDisable::class.java)
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
}
