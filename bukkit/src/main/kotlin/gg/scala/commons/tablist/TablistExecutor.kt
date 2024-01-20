package gg.scala.commons.tablist

import gg.scala.commons.tablist.adapter.TabElementProxy
import gg.scala.commons.tablist.manage.TablistManager
import io.github.nosequel.tab.shared.TabHandler
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.utils.Players

/**
 * @author GrowlyX
 * @since 8/8/2022
 */
object TablistExecutor : Runnable
{
    private val proxy = TabElementProxy()

    override fun run()
    {
//        val displayingTo = Players.all()
//            .filter {
//                TablistService.displaying.contains(it.uniqueId)
//            }

        for (handle in TablistManager.get().tablistHandles)
        {
            proxy.setActiveHandle(handle)

            kotlin.runCatching {
                TablistService.populator?.populate(handle.holder, proxy)
                handle.onTick()
            }.onFailure {
                it.printStackTrace()
            }

//            TablistService.cachedElements[player.uniqueId] = element
        }
    }
}
