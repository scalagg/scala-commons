package gg.scala.cloudsync.command.commands

import gg.scala.cloudsync.connection.CloudSyncConnector
import gg.scala.cloudsync.wrapper.Repositories
import gg.scala.cloudsync.wrapper.component.ComponentWrapper
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.*
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@CommandAlias("cs|cloudsync")
@CommandPermission("cloudsync.command")
object CloudSyncCommand : BaseCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("updates")
    @Description("View all pending plugin updates.")
    fun onUpdates(player: Player)
    {
        player.sendMessage("${CC.GREEN}Finding pending plugin updates...")

        CloudSyncConnector.usePendingUpdates { name, asset, _ ->
            val fancyMessage = FancyMessage()
            fancyMessage.withMessage("${CC.D_AQUA}$name${CC.YELLOW} is outdated! The newest version is: ${CC.GREEN}${asset.maven2!!.version}${CC.YELLOW}.")
            fancyMessage.sendToPlayer(player)
        }
    }

    @Subcommand("information")
    @Description("View all information for a component.")
    fun onInformation(
        player: Player, @Single component: String,
        @Single module: String
    ): CompletableFuture<Void>
    {
        player.sendMessage("${CC.GREEN}Finding plugin versions...")

        return Repositories.asyncComponents().thenAcceptAsync {
            handleInternalInformation(player, it.items, component, module)
        }
    }

    private fun handleInternalInformation(
        player: Player, items: MutableList<ComponentWrapper>,
        asset: String, module: String
    )
    {
        val component = items.firstOrNull {
            it.group.equals(asset, true) &&
                    it.name.equals(module, true)
        } ?: throw ConditionFailedException(
            "There is no item with that group id. ${CC.D_RED}(ex. gg.scala.cloudsync & cloudsync)"
        )

        player.sendMessage("${CC.B_YELLOW}Viewing information for [$asset:$module]:")
        player.sendMessage("${CC.YELLOW}Latest version: ${CC.AQUA}${component.version}")
        player.sendMessage("")
        player.sendMessage("${CC.B_YELLOW}Available Assets:")

        for (compAsset in component.assets)
        {
            player.sendMessage("${CC.WHITE} - ${compAsset.maven2?.extension ?: "N/A"}")
            player.sendMessage("   ${CC.YELLOW}Checksum ${CC.GRAY}(md5)${CC.YELLOW}: ${CC.AQUA}${compAsset.checksum.md5}")
        }
    }
}
