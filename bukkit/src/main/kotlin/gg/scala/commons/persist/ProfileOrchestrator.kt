package gg.scala.commons.persist

import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import me.lucko.helper.Events
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
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
        controller = DataStoreObjectControllerCache
            .create(type())

        Events.subscribe(AsyncPlayerPreLoginEvent::class.java).handler {
            controller.loadOptimalCopy(
                it.uniqueId
            ) {
                new(it.uniqueId)
            }
        }

        Events.subscribe(PlayerQuitEvent::class.java).handler {
            controller.localCache().remove(it.player.uniqueId)
                ?.let { cached ->
                    this.controller.save(cached, DataStoreStorageType.MONGO)
                }
        }
    }

    abstract fun type(): KClass<T>
    abstract fun new(uniqueId: UUID): T
}
