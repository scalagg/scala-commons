package gg.scala.commons.tablist

import io.github.nosequel.tab.shared.entry.TabElement
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface TablistPopulator
{
    fun displayPreProcessor(player: Player): CompletableFuture<Boolean>
    fun populate(player: Player, element: TabElement)
}
