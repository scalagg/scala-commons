package gg.scala.commons

import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.annotations.Listeners
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizers
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.annotations.custom.CustomAnnotationProcessors
import gg.scala.commons.annotations.inject.AutoBind
import gg.scala.commons.annotations.inject.IgnoreProvidedFlavor
import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.library.HelperLibraryHandler
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorBinder
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.Schedulers
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.util.CC
import org.apache.commons.lang.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Method
import java.util.logging.Level
import java.util.logging.Logger

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
        this.flavor.reflections
    }

    private lateinit var flavor: Flavor
    private lateinit var commandManager: CubedCommandManager

    private val ignoreFlavor = this::class.java
        .getAnnotation(IgnoreProvidedFlavor::class.java) != null

    fun flavor() = flavor

    fun flavor(
        lambda: Flavor.() -> Unit
    )
    {
        if (ignoreFlavor)
            return

        flavor.lambda()
    }

    fun supplyFlavor(
        flavor: Flavor
    )
    {
        this.flavor = flavor
    }

    override fun load()
    {
        val java11Enabled = SystemUtils
            .isJavaVersionAtLeast(11)

        if (!java11Enabled)
        {
            logger.info(
                "[Compatability] This version of ${description.name} does not support java ${
                    SystemUtils.getJavaVersion()
                }!"
            )
            logger.info("[Compatability] Please use a version higher than 11.")

            Bukkit.shutdown()
            return
        }

        this.flavor = Flavor.create(
            this::class, FlavorOptions(logger)
        )

        this.flavor {
            binders.add(
                FlavorBinder(this::class) to this
            )

            bind<ExtendedScalaPlugin>() to this@ExtendedScalaPlugin
            bind<JavaPlugin>() to this@ExtendedScalaPlugin

            bind<Server>() to server
            bind<Logger>() to logger
        }

        this.packageIndexer
            .getTypesAnnotatedWith<LibraryLoader>()
            .mapNotNull {
                it.objectInstance()
            }
            .forEach {
                HelperLibraryHandler
                    .loadAll(it::class.java)
            }
    }

    override fun enable()
    {
        this.logger.info("Starting container enable process...")

        this.packageIndexer
            .getTypesAnnotatedWith<AutoBind>()
            .forEach {
                kotlin.runCatching {
                    val instance = it.objectInstance()
                        ?: it.newInstance()

                    this.flavor.binders.add(
                        FlavorBinder(instance::class) to instance
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to AutoBind", it)
                }
            }

        this.packageIndexer
            .getMethodsAnnotatedWith<AutoBind>()
            .forEach {
                kotlin.runCatching {
                    val instance = it.invoke(
                        it.declaringClass.objectInstance()!!
                    )

                    this.flavor.binders.add(
                        FlavorBinder(instance::class) to instance
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to AutoBind", it)
                }
            }

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

        val customized = mutableListOf<Method>()

        for (customizer in CommandManagerCustomizers.customizers)
        {
            customizer.methods
                .filter {
                    it.getAnnotation(CommandManagerCustomizer::class.java) != null
                }
                .forEach { method ->
                    kotlin.runCatching {
                        method.invoke(
                            customizer.objectInstance()
                                ?: customizer.newInstance(),
                            this.commandManager
                        )
                    }.onFailure {
                        this.logger.log(Level.WARNING, "Failed to customize command manager", it)
                    }.onSuccess {
                        customized.add(method)

                        this.logger.info(
                            "[Commands] [Default] Customized command manager through ${
                                method.declaringClass.simpleName
                            }#${
                                method.name
                            }()."
                        )
                    }
                }
        }

        this.packageIndexer
            .getMethodsAnnotatedWith<CommandManagerCustomizer>()
            .filter {
                !customized.contains(it)
            }
            .forEach { method ->
                val declaring = method.declaringClass
                    .objectInstance() ?: method.declaringClass.newInstance()

                this.flavor.inject(declaring)

                kotlin.runCatching {
                    method.invoke(
                        declaring, this.commandManager
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to customize command manager", it)
                }.onSuccess {
                    logger.info(
                        "[Commands] Customized command manager through ${
                            method.declaringClass.simpleName
                        }#${
                            method.name
                        }()."
                    )
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
                this.flavor.inject(it)

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
                this.flavor.inject(it)

                val annotation = it.javaClass
                    .getAnnotation(Repeating::class.java)!!

                Schedulers.get(annotation.context)
                    .runRepeating(
                        it,
                        annotation.delay,
                        annotation.value
                    )
            }

        val processors =
            CustomAnnotationProcessors
                .processors

        for (processor in processors)
        {
            this.packageIndexer.reflections
                .getTypesAnnotatedWith(processor.key.java)
                .map {
                    it.objectInstance() ?: it.newInstance()
                }
                .forEach {
                    this.logger.info(
                        "[Processor] Processing ${
                            it.javaClass.simpleName
                        } with @${
                            processor.key.java.simpleName
                        }."
                    )

                    processor.value.invoke(it)
                }
        }

        this.flavor.startup()
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
        this.flavor.close()
    }

    private fun Class<*>.objectInstance(): Any?
    {
        return kotlin.runCatching {
            getDeclaredField("INSTANCE").get(null)
        }.getOrNull()
    }
}
