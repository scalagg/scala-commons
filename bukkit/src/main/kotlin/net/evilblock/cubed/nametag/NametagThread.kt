package net.evilblock.cubed.nametag

import net.evilblock.cubed.nametag.update.NametagUpdate
import java.util.concurrent.ConcurrentHashMap

internal class NametagThread : Runnable
{
    override fun run()
    {
        val updatesIterator = pendingUpdates.keys.toList()

        for (pendingUpdate in updatesIterator)
        {
            runCatching {
                NametagHandler.applyUpdate(pendingUpdate)
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
