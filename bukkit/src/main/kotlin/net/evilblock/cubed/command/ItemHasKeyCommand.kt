/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Flags
import co.aikar.commands.annotation.Name
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.entity.Player

object ItemHasKeyCommand : BaseCommand() {

    @CommandAlias("item-has-key")
    @CommandPermission("op")
    fun execute(@Flags("itemheld") player: Player, @Name("key") key: String) {
        val itemInHand = player.inventory.itemInHand

        if (ItemUtils.itemTagHasKey(itemInHand, key)) {
            player.sendMessage("${CC.GREEN}Has key!")
        } else {
            player.sendMessage("${CC.RED}Doesn't have key!")
        }
    }
}