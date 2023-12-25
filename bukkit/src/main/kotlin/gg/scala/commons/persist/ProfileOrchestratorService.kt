package gg.scala.commons.persist

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.store.storage.type.DataStoreStorageType
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/24/2023
 */
object ProfileOrchestratorService
{
    private val registry = mutableListOf<ProfileOrchestrator<*>>()
    fun registerSelf(orchestrator: ProfileOrchestrator<*>)
    {
        registry += orchestrator
    }

    fun configureEventHandlers(plugin: ExtendedScalaPlugin)
    {
        Events
            .subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler {
                CompletableFuture
                    .allOf(
                        *(registry.map { orchestrator ->
                            orchestrator.loadCopyOf(it.uniqueId)
                                .thenRunAsync {
                                    orchestrator.postLoad(it.uniqueId)
                                }
                        }).toTypedArray()
                    )
                    .join()
            }
            .bindWith(plugin)

        Events
            .subscribe(PlayerLoginEvent::class.java)
            .handler {
                val profilesNotLoaded = registry
                    .filterNot { orchestrator ->
                        orchestrator.controller.localCache()
                            .containsKey(it.player.uniqueId)
                    }

                if (profilesNotLoaded.isNotEmpty())
                {
                    it.disallow(
                        PlayerLoginEvent.Result.KICK_OTHER,
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
                    *registry
                        .map { orchestrator ->
                            orchestrator.logoutSequenceFor(it.player.uniqueId)
                        }
                        .toTypedArray()
                )
            }
            .bindWith(plugin)
    }
}
