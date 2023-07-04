package gg.scala.commons.persist

import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/16/2022
 */
abstract class ProfileOrchestrator<T : IDataStoreObject>
{
    private lateinit var controller: DataStoreObjectController<T>

    fun find(player: Player): T? =
        controller.localCache()[player.uniqueId]

    fun find(uniqueId: UUID): T? =
        controller.localCache()[uniqueId]

    fun subscribe()
    {
        try
        {
            // we don't want to reinitialize the controller
            controller
            return
        } catch (_: Exception)
        {

        }

        controller = DataStoreObjectControllerCache
            .create(type())

        Events
            .subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler {
                controller
                    .loadOptimalCopy(
                        it.uniqueId
                    ) {
                        new(it.uniqueId)
                    }.join()

                this.postLoad(it.uniqueId)
            }

        Events
            .subscribe(PlayerLoginEvent::class.java)
            .handler {
                if (!controller.localCache().containsKey(it.player.uniqueId))
                {
                    it.disallow(
                        PlayerLoginEvent.Result.KICK_OTHER,
                        "${CC.RED}Sorry! One of your profiles failed to load:\n${
                            type().java.name.split(".")
                                .joinToString(".") { component ->
                                    if (component.first().isUpperCase()) component else component.first().toString()
                                }
                        }\n\n${CC.GRAY}Please report this to an administrator."
                    )
                }
            }

        Events
            .subscribe(PlayerQuitEvent::class.java, EventPriority.LOWEST)
            .handler {
                controller
                    .localCache()
                    .remove(it.player.uniqueId)
                    ?.let { cached ->
                        this.controller.save(cached, DataStoreStorageType.MONGO)
                            .exceptionally { throwable ->
                                throwable.printStackTrace()
                                return@exceptionally null
                            }
                    }
            }
    }

    open fun postLoad(uniqueId: UUID)
    {

    }

    abstract fun type(): KClass<T>
    abstract fun new(uniqueId: UUID): T
}
