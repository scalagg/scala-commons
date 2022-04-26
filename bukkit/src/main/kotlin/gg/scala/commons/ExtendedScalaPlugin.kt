package gg.scala.commons

import co.aikar.commands.BaseCommand
import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.PaperCommandManager
import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.annotations.Listeners
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizers
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.annotations.container.ContainerPreEnable
import gg.scala.commons.annotations.container.flavor.LazyStartup
import gg.scala.commons.annotations.custom.CustomAnnotationProcessors
import gg.scala.commons.annotations.inject.AutoBind
import gg.scala.commons.annotations.inject.IgnoreProvidedFlavor
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.commons.annotations.scripts.RequiresScripts
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.config.ConfigContainerized
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.library.HelperLibraryHandler
import gg.scala.commons.localization.container.LocalizationContainerContextual
import gg.scala.commons.localization.container.LocalizationContainerized
import gg.scala.commons.plugin.PluginBridge
import gg.scala.commons.preconfigure.PreConfigureSubTypeProcessor
import gg.scala.commons.quality.PluginQolInternal
import gg.scala.commons.scripts.ScriptPluginService
import gg.scala.commons.scripts.binding.Scripts
import gg.scala.commons.scripts.config.ScriptConfig
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
import java.io.File
import java.lang.reflect.Method
import java.util.logging.Level
import java.util.logging.Logger

/**
 * An extension of [ExtendedJavaPlugin] with
 * support for our custom annotation-based
 * plugin framework.
 *
 * @author GrowlyX
 * @since 10/15/2021
 */
open class ExtendedScalaPlugin : ExtendedJavaPlugin()
{
    val packageIndexer by lazy {
        this.flavor.reflections
    }

    private lateinit var flavor: Flavor
    private lateinit var commandManager: CubedCommandManager

    val configContainerized by lazy {
        ConfigContainerized(this)
    }

    val localizationContainerized by lazy {
        LocalizationContainerized(this)
    }

    val containerized by lazy {
        mutableListOf(
            configContainerized, localizationContainerized
        )
    }

    lateinit var qolInternal: PluginQolInternal

    private val ignoreInjector = this::class.java
        .getAnnotation(IgnoreProvidedFlavor::class.java) != null

    fun flavor() = flavor

    fun flavor(
        lambda: Flavor.() -> Unit
    )
    {
        if (ignoreInjector)
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

        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerPreEnable>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to PreEnable container part", it)
                }
            }

        flavor().binders.add(
            FlavorBinder(
                this@ExtendedScalaPlugin::class
            ) to this@ExtendedScalaPlugin
        )

        this.flavor {
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

        for (containerized in this.containerized)
        {
            containerized.inject()
        }

        if (
            this::class.java
                .getAnnotation(
                    RequiresScripts::class.java
                ) != null
        )
        {
            val scriptFile = File(
                this.dataFolder, "scripts"
            )

            this.configContainerized
                .inject(
                    ContainerConfig("scripts", ScriptConfig::class)
                )

            if (!scriptFile.exists())
            {
                scriptFile.mkdirs()
            }
        }
    }

    override fun enable()
    {
        this.logger.info("Starting container initialization process...")

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

        for (containerized in this.containerized)
        {
            containerized.loadAll()
        }

        if (
            this::class.java
                .getAnnotation(
                    RequiresScripts::class.java
                ) != null
        )
        {
            val scriptService =
                ScriptPluginService(this)

            scriptService.enable()

            this.flavor {
                bind<Scripts>() to Scripts(scriptService)
            }
        }

        this.qolInternal = PluginQolInternal(this)
        this.qolInternal.load()

        flavor().bind<PluginQolInternal>() to this.qolInternal

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

        flavor {
            bind<CubedCommandManager>() to commandManager
            bind<BukkitCommandManager>() to commandManager
            bind<PaperCommandManager>() to commandManager
        }

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
                            "[Commands] Command manager customization applied from ${
                                method.declaringClass.simpleName
                            }."
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
                        "[Commands] Command manager customization applied from ${
                            method.declaringClass.simpleName
                        }."
                    )
                }
            }

        this.packageIndexer
            .getTypesAnnotatedWith<AutoRegister>()
            .forEach {
                kotlin.runCatching {
                    val instance = it.objectInstance()
                        ?: it.newInstance()

                    this.flavor.inject(instance)

                    this.commandManager.registerCommand(
                        instance as BaseCommand
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
            .getTypesAnnotatedWith<SoftDependency>()
            .filter {
                this.server.pluginManager
                    .isPluginEnabled(
                        it.getAnnotation(
                            SoftDependency::class.java
                        ).value
                    )
            }
            .forEach { clazz ->
                kotlin.runCatching {
                    this.flavor.inject(
                        clazz.objectInstance() ?: clazz.newInstance()
                    )
                }.onSuccess {
                    logger.info(
                        "[Feature] Enabled feature ${
                            clazz.simpleName
                        } depending on plugin ${
                            clazz.getAnnotation(SoftDependency::class.java).value
                        }!"
                    )
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to enable dependant feature", it)
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
                    .bindWith(this)
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
                        } annotation @${
                            processor.key.java.simpleName
                        }."
                    )

                    processor.value.invoke(it)
                }
        }

        PreConfigureSubTypeProcessor
            .processLocalSubTypeServices(this)

        if (
            this::class.java
                .getAnnotation(LazyStartup::class.java) == null
        )
        {
            this.flavor.startup()
        }

        PluginBridge.install(this)
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

        PluginBridge.invalidate(this)

        this.commandManager
            .unregisterCommands()

        this.flavor.close()
    }

    inline fun <reified T> config(): T
    {
        val container = this
            .configContainerized

        val lateInject = container
            .waitingLateInject
            .find {
                it.model == T::class.java
            }

        if (lateInject != null)
        {
            container.inject(lateInject)
            container.container<T>().load()
        }

        return this.configContainerized
            .container<T>().getModelAs()
    }

    fun localized(identifier: String): LocalizationContainerContextual
    {
        return this.localizationContainerized.container(identifier)
    }

    inline fun <reified T> contract(): T?
    {
        val subTypes = this
            .packageIndexer
            .getSubTypes<T>()
            .firstOrNull()
            ?: return null

        return subTypes.objectInstance() as T
            ?: subTypes.newInstance() as T
    }

    fun Class<*>.objectInstance(): Any?
    {
        return kotlin.runCatching {
            getDeclaredField("INSTANCE").get(null)
        }.getOrNull()
    }
}
