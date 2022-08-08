package gg.scala.commons.tablist

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import io.github.nosequel.tab.shared.TabHandler
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/8/2022
 */
@Service
object TablistService : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    val cachedElements = ConcurrentHashMap<UUID, TabElement>()
    val displaying = mutableSetOf<UUID>()

    private var handler: TabHandler? = null
    var populator: TablistPopulator? = null

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                this.populator
                    ?.displayPreProcessor(it.player)
                    ?.thenAccept { display ->
                        if (display)
                        {
                            this.displaying.add(it.player.uniqueId)
                        }
                    }
            }
            .bindWith(this.plugin)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                this.displaying.remove(it.player.uniqueId)
            }
            .bindWith(this.plugin)

        this.scheduleAtFixedRate(
            TablistExecutor, 0L, 250L, TimeUnit.MILLISECONDS
        )
    }

    fun register(populator: TablistPopulator)
    {
        val fallbackElement = TabElement()
        this.populator = populator

        this.handler = TabHandler({
            this.cachedElements[it.uniqueId] ?: fallbackElement
        }, this.plugin, 5L)
    }
}
