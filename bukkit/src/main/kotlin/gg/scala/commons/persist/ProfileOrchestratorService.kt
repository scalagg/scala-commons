package gg.scala.commons.persist

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.ExtendedScalaPlugin
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import org.bukkit.Bukkit
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

object ProfileOrchestratorService
{
    private val registry = mutableMapOf<Class<*>, ProfileOrchestrator<*>>()
    fun registerSelf(orchestrator: ProfileOrchestrator<*>)
    {
        registry[orchestrator.type().java] = orchestrator
    }

    val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("backend:profiles")
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    fun configureEventHandlers(plugin: ExtendedScalaPlugin)
    {
        Events
            .subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler {
                CompletableFuture
                    .allOf(
                        *(registry.values.map { orchestrator ->
                            orchestrator.loadCopyOf(it.uniqueId)
                                .thenRunAsync {
                                    orchestrator.postLoad(it.uniqueId)
                                }
                        }).toTypedArray()
                    )
                    .join()

                val profilesNotLoaded = registry.values
                    .filterNot { orchestrator ->
                        orchestrator.controller.localCache()
                            .containsKey(it.uniqueId)
                    }

                if (profilesNotLoaded.isNotEmpty())
                {
                    it.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        "${CC.RED}Sorry! One or more of your profiles failed to load:\n${
                            profilesNotLoaded
                                .joinToString("\n") { orchestrator ->
                                    orchestrator.type().java.name.split(".")
                                        .joinToString(".") { component ->
                                            if (component.first().isUpperCase()) component else component.first().toString()
                                        }
                                }
                        }\n${CC.GRAY}Please report this to an administrator."
                    )
                }
            }
            .bindWith(plugin)

        Events
            .subscribe(PlayerQuitEvent::class.java, EventPriority.LOWEST)
            .handler {
                CompletableFuture.allOf(
                    *registry.values
                        .map { orchestrator ->
                            orchestrator.logoutSequenceFor(it.player.uniqueId)
                        }
                        .toTypedArray()
                )
            }
            .bindWith(plugin)

        aware.listen("update") {
            val uniqueId = retrieve<UUID>("player")
            val type = Reflection
                .getClassSuppressed(retrieve<String>("type"))
                ?: return@listen
            Bukkit.getPlayer(uniqueId) ?: return@listen

            val registrar = registry[type]
                ?: return@listen

            with(registrar) {
                updateLocalCache(uniqueId)
            }
        }
        aware.connect().toCompletableFuture().join()
    }
}
