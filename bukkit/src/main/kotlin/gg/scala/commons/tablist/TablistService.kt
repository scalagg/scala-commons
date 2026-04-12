package gg.scala.commons.tablist

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsTablistConfig
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
object TablistService : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
{
    @Inject
    lateinit var commons: ScalaCommonsSpigot

    var populator: TablistPopulator? = null
    var tabListTicksCalculator: (Player) -> Long? = { null }

    private val systemProvider: TablistSystemProvider = BuiltInV1TablistSystemProvider

    @Configure
    fun configure()
    {
        val config = commons.config<CommonsTablistConfig>()

        if (!config.enabled)
        {
            commons.logger.info("Skipping tablist initialization!")
            return
        }

        systemProvider.configure()

        scheduleAtFixedRate(
            TablistExecutor, 0L, 500L, TimeUnit.MILLISECONDS
        )

        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                var ticks = tabListTicksCalculator.invoke(it.player)

                if (
                    config.continueInitializationIfNullInvokation &&
                    ticks == null
                )
                {
                    ticks = config.tablistInitializeTicks.toLong()
                } else
                {
                    return@handler
                }

                Tasks.delayed(ticks) {
                    systemProvider.initialize(it.player)

                    this.populator
                        ?.displayPreProcessor(it.player)
                        ?.thenAccept { display ->
                            systemProvider.create(it.player, display)
                        }
                }
            }
            .bindWith(commons)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                systemProvider.destroy(it.player)
            }
            .bindWith(commons)
    }

    fun register(populator: TablistPopulator)
    {
        this.populator = populator

    }
}
