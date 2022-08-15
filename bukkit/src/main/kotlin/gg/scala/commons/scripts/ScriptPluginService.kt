package gg.scala.commons.scripts

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.scripts.ScriptImports
import gg.scala.commons.scripts.config.ScriptConfig
import io.github.classgraph.ClassGraph
import me.lucko.helper.Schedulers
import me.lucko.helper.internal.LoaderUtils
import me.lucko.helper.scheduler.Ticks
import me.lucko.scriptcontroller.ScriptController
import me.lucko.scriptcontroller.environment.ScriptEnvironment
import me.lucko.scriptcontroller.environment.settings.EnvironmentSettings
import me.lucko.scriptcontroller.logging.SystemLogger
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
class ScriptPluginService(
    private val plugin: ExtendedScalaPlugin
)
{
    companion object
    {
        @JvmStatic
        private val DEFAULT_IMPORTS =
            listOf(
                // include all the packages in helper
                "me.lucko.helper",
                "com.flowpowered.math",
                "net.jodah.expiringmap",
                // include all the packages in bukkit
                "org.bukkit",
                "com.destroystokyo.paper",
                "org.spigotmc.event"
            )
    }

    private lateinit var controller: ScriptController
    private lateinit var environment: ScriptEnvironment

    fun enable()
    {
        val initialization = measureTimeMillis {
            val scriptConfig = plugin
                .config<ScriptConfig>()

            val imports = plugin.javaClass
                .getAnnotation(
                    ScriptImports::class.java
                )

            plugin.logger.info("[Scripts] Loading script controller...")

            val classGraph = ClassGraph()
                .acceptPackages(
                    *(imports?.value ?: DEFAULT_IMPORTS.toTypedArray())
                )
                .addClassLoader(
                    plugin.server::class.java
                        .classLoader
                )

            LoaderUtils.getHelperImplementationPlugins()
                .forEach {
                    val classLoader = it
                        .javaClass.classLoader

                    classGraph
                        .addClassLoader(classLoader)
                }

            val defaultPackages =
                classGraph.scan()
                    .allClasses
                    .map { it.packageName }

            val pollIntervalTimeUnit = TimeUnit
                .valueOf(scriptConfig.pollIntervalTimeUnit)

            plugin.logger.info("[Scripts] Creating new script controller...")

            controller = ScriptController.builder()
                .logger(
                    SystemLogger
                        .usingJavaLogger(plugin.logger)
                )
                .defaultEnvironmentSettings(
                    EnvironmentSettings.builder()
                        .loadExecutor(ScriptLoadExecutor)
                        .runExecutor(Schedulers.sync())
                        .pollRate(
                            Ticks.to(
                                scriptConfig.pollInterval.toLong(),
                                pollIntervalTimeUnit
                            ),
                            pollIntervalTimeUnit
                        )
                        .initScript(scriptConfig.initScript)
                        .withBindings(
                            ScriptBindings.GeneralScriptBindings()
                        )
                        .withBindings(
                            ScriptBindings.HelperScriptBindings(plugin)
                        )
                        .withDefaultPackageImports(defaultPackages)
                        .build()
                )
                .build()

            val directory = File(
                this.plugin.dataFolder,
                "scripts"
            ).toPath()

            plugin.logger.info("[Scripts] Creating new script environment...")

            this.environment = this.controller
                .setupNewEnvironment(directory)
        }

        plugin.logger.info("[Scripts] Completed script initialization in ${initialization}ms!")
    }
}
