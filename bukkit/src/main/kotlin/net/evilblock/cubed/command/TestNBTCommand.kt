package net.evilblock.cubed.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils

/**
 * @author GrowlyX
 * @since 10/6/2024
 */
@AutoRegister
@CommandAlias("testnbt")
@CommandPermission("op")
object TestNBTCommand : ScalaCommand()
{
    @Default
    fun testNBT(player: ScalaPlayer)
    {
        val item = ItemBuilder
            .of(XMaterial.BOOK)
            .name("Test NBT")
            .build()

        player.sendMessage("Adding tag:")
        ItemUtils.addToItemTag(item, "horse", "dog", true)

        player.sendMessage("Reading tag: ${ItemUtils.readItemTagKey(item, "horse")}")
    }
}
