package gg.scala.commons.tablist

import io.github.nosequel.tab.shared.entry.TabElement
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 8/8/2022
 */
interface TablistPopulator
{
    fun displayPreProcessor(player: Player): CompletableFuture<Boolean>
    fun populate(player: Player, element: TabElement)
}
