package gg.scala.commons.tablist

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import io.github.nosequel.tab.shared.TabHandler
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.Events
import me.lucko.helper.utils.Players
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsTablistConfig
import net.evilblock.cubed.entity.npc.protocol.NpcProtocol
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

    var tabListTicksCalculator: (Player) -> Long? = { null }

    fun removePlayerFor(toBeRemoved: Player, removedFor: Player)
    {
        val removePacket = NpcProtocol.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(
            Reflection.getEnum(NpcProtocol.ENUM_PLAYER_INFO_ACTION_CLASS, "REMOVE_PLAYER")!!,
            arrayListOf(MinecraftReflection.getHandle(toBeRemoved))
        )

        MinecraftProtocol.send(removedFor, removePacket)
    }

    @Configure
    fun configure()
    {
        val config = plugin.config<CommonsTablistConfig>()

        if (!config.enabled)
        {
            plugin.logger.info("Skipping tablist initialization!")
            return
        }

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
                    this.populator
                        ?.displayPreProcessor(it.player)
                        ?.thenAccept { display ->
                            for (player in Players.all())
                            {
                                removePlayerFor(player, it.player)
                                if (player.uniqueId in displaying)
                                {
                                    removePlayerFor(it.player, player)
                                }
                            }

                            if (display)
                            {
                                this.displaying.add(it.player.uniqueId)
                            }
                        }
                }
            }
            .bindWith(this.plugin)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                this.cachedElements.remove(it.player.uniqueId)
                this.displaying.remove(it.player.uniqueId)
            }
            .bindWith(this.plugin)

        this.scheduleAtFixedRate(
            TablistExecutor, 0L, 50L, TimeUnit.MILLISECONDS
        )
    }

    fun register(populator: TablistPopulator)
    {
        this.populator = populator

        this.handler = TabHandler({
            if (it.uniqueId !in this.displaying)
            {
                return@TabHandler null
            }

            this.cachedElements[it.uniqueId]
        }, this.plugin, 2L)
    }
}
