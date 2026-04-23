package net.evilblock.cubed.nametag

import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.nametag.update.NametagUpdate
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentHashMap

internal class NametagThread : Runnable
{
    override fun run()
    {
        val updatesIterator = pendingUpdates.keys.toList()

        for (pendingUpdate in updatesIterator)
        {
            runCatching {
                Bukkit.getScheduler().runTask(ScalaCommonsSpigot.instance) {
                    runCatching {
                        NametagHandler.applyUpdate(pendingUpdate)
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
                pendingUpdates.remove(pendingUpdate)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    companion object
    {
        var pendingUpdates = ConcurrentHashMap<NametagUpdate, Boolean>()
    }
}
