package net.evilblock.cubed.protocol

import com.viaversion.viaversion.api.Via
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.commons.protocol.ProtocolVersion
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/14/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("ViaVersion")
object ViaVersionVersionProvider : ProtocolVersion
{
    @Configure
    fun configure()
    {
        ScalaCommonsSpigot.instance.versionProvider = this
    }

    override fun getProtocolVersion(player: Player) =
        Via.getAPI().getPlayerVersion(player.uniqueId)
}
