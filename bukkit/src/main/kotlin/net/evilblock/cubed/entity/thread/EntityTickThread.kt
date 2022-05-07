package net.evilblock.cubed.entity.thread

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.tick.TickableEntity
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.GameMode

@Service
object EntityTickThread : Thread("Commons - Entities")
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    var DEBUG = false

    var thrownExceptions: Int = 0
    var lastAlert: Long? = null

    var ticks: Long = 0L

    override fun run()
    {
        while (true)
        {
            try
            {
                tickEntities()

                if (ticks++ % 10 == 0L)
                {
                    updateWatchers()
                }
            } catch (e: Exception)
            {
                e.printStackTrace()
            }

            sleep(50L)
        }
    }

    private fun tickEntities()
    {
        if (lastAlert == null || System.currentTimeMillis() >= lastAlert!! + 60_000L)
        {
            if (thrownExceptions > 0)
            {
                Bukkit.broadcast(
                    "${CC.RED}[System] ${CC.D_RED}${Numbers.format(thrownExceptions)}${CC.RED} exceptions were thrown in the last ${CC.BOLD}60 seconds${CC.RED}.",
                    "network.system-admin"
                )
            }

            thrownExceptions = 0
            lastAlert = System.currentTimeMillis()
        }

        for (entity in EntityHandler.getEntities())
        {
            try
            {
                if (entity is TickableEntity)
                {
                    entity.tick()
                }

                if (entity.animation != null)
                {
                    val animation = entity.animation!!
                    if (animation.lastTick == null || System.currentTimeMillis() - animation.lastTick!! >= animation.getInterval())
                    {
                        animation.current++
                        animation.lastTick = System.currentTimeMillis()
                        animation.tick()
                    }
                }
            } catch (e: Exception)
            {
                if (DEBUG)
                {
                    e.printStackTrace()
                } else
                {
                    thrownExceptions++
                }
            }
        }
    }

    private fun updateWatchers()
    {
        for (player in Bukkit.getOnlinePlayers())
        {
            if (!player.isOnline)
            {
                continue
            }

            val showDebug = (player.isOp || player.hasPermission("commons.entities.debug"))
                    && player.gameMode == GameMode.CREATIVE
                    && EntityHandler.isDebugViewEnabled(player)

            for (entity in EntityHandler.getEntities())
            {
                try
                {
                    val isVisible = entity.isVisibleToPlayer(player)
                    val currentlyWatching = entity.isWatcher(player.uniqueId)

                    if (isVisible && !currentlyWatching)
                    {
                        entity.spawn(player)
                    } else if (!isVisible && currentlyWatching)
                    {
                        entity.destroy(player)
                    }
                } catch (e: Exception)
                {
                    plugin.logger.warning("Failed to update entity ${entity.uuid} for player ${player.name}")
                    e.printStackTrace()
                }

                if (entity.isWatcher(player.uniqueId))
                {
                    if (showDebug && entity.canDisplayDebug())
                    {
                        if (!entity.debugView.containsKey(player.uniqueId))
                        {
                            try
                            {
                                val debugHologram = HologramEntity(text = "", location = entity.getDebugViewLocation())
                                debugHologram.initializeData()
                                debugHologram.updateLines(entity.renderDebugInformation())

                                val attachedHologram = entity.getAttachedHologram()
                                if (attachedHologram != null)
                                {
                                    debugHologram.offset = attachedHologram.offset
                                }

                                debugHologram.spawn(player)

                                entity.debugView[player.uniqueId] = debugHologram
                            } catch (e: Exception)
                            {
                                plugin.logger.warning("Failed to update debug entity ${entity.uuid} for player ${player.name}")
                                e.printStackTrace()
                            }
                        }
                    } else
                    {
                        if (entity.debugView.containsKey(player.uniqueId))
                        {
                            entity.debugView.remove(player.uniqueId)!!.destroy(player)
                        }
                    }
                }
            }

            if (EntityHandler.isTrackingInstanceEntities(player))
            {
                for (entity in EntityHandler.getInstanceEntities(player))
                {
                    try
                    {
                        val isVisible = entity.isVisibleToPlayer(player)
                        val currentlyWatching = entity.isWatcher(player.uniqueId)

                        if (isVisible && !currentlyWatching)
                        {
                            entity.spawn(player)
                        } else if (!isVisible && currentlyWatching)
                        {
                            entity.destroy(player)
                        }
                    } catch (e: Exception)
                    {
                        plugin.logger.warning("Failed to update instance entity ${entity.uuid} for player ${player.name}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}
