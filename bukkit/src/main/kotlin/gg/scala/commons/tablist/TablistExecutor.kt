package gg.scala.commons.tablist

import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.utils.Players

/**
 * @author GrowlyX
 * @since 8/8/2022
 */
object TablistExecutor : Runnable
{
    override fun run()
    {
        val displayingTo = Players.all()
            .filter {
                TablistService.displaying.contains(it.uniqueId)
            }

        for (player in displayingTo)
        {
            val element = TabElement()

            kotlin.runCatching {
                TablistService.populator?.populate(player, element)
            }.onFailure {
                it.printStackTrace()
            }

            TablistService.cachedElements[player.uniqueId] = element
        }
    }
}
