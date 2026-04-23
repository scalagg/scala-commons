package gg.scala.commons.tablist

import club.frozed.tablist.FrozedTablist
import club.frozed.tablist.TabLayout
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.Helper
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 2/19/2024
 */
object BuiltInV2TablistSystemProvider : TablistSystemProvider
{
    private var frozedTablist: FrozedTablist? = null
    override fun configure()
    {
        frozedTablist = FrozedTablist(
            Helper.hostPlugin() as JavaPlugin,
            {
                val element = TabElement()

                TablistService.populator
                    ?.populate(it, element)
                    ?.let { element }
            },
            0, 10
        )
    }

    override fun initialize(player: Player)
    {
    }

    override fun create(player: Player, display: Boolean)
    {
        if (frozedTablist == null)
        {
            return
        }

        if (display)
        {
            val tabLayout = TabLayout(frozedTablist, player)
            tabLayout.create()

            TabLayout.getLayoutMapping()[player.uniqueId] = tabLayout
        }
    }

    override fun destroy(player: Player)
    {
        frozedTablist?.deregisterPlayer(player)
    }
}
