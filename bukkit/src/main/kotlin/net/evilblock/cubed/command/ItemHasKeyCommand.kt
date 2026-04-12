

package net.evilblock.cubed.command

import co.aikar.commands.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("item-has-key")
@CommandPermission("op")
object ItemHasKeyCommand : ScalaCommand()
{
    @Default
    fun execute(
        @Flags("itemheld") player: Player, key: String
    )
    {
        val itemInHand = player.inventory.itemInHand

        if (ItemUtils.itemTagHasKey(itemInHand, key))
        {
            player.sendMessage("${CC.GREEN}Has key!")
        } else
        {
            player.sendMessage("${CC.RED}Doesn't have key!")
        }
    }
}
