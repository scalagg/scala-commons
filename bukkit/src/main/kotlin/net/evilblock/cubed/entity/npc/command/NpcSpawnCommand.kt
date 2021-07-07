package net.evilblock.cubed.entity.npc.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.floor

object NpcSpawnCommand {

    @Command(
        names = ["npc spawn defined"],
        description = "Spawns an NPC defined in the Cubed config",
        permission = "cubed.npcs.create",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") id: String) {
        val config = Cubed.instance.config
        if (!config.contains("defined-npcs.$id")) {
            player.sendMessage("${ChatColor.RED}That NPC hasn't been defined!")
            return
        }

        val loc = player.location
        loc.x = floor(loc.x) + 0.5
        loc.z = floor(loc.z) + 0.5

        val npc = NpcEntity(config.getStringList("defined-npcs.$id.lines").map { ChatColor.translateAlternateColorCodes('&', it) }, loc)
        npc.initializeData()

        if (config.contains("defined-npcs.$id.texture-value") && config.contains("defined-npcs.$id.texture-signature")) {
            npc.updateTexture(config.getString("defined-npcs.$id.texture-value"), config.getString("defined-npcs.$id.texture-signature"))
        } else if (config.contains("defined-npcs.$id.texture")) {
            npc.updateTextureByUsername(config.getString("defined-npcs.$id.texture")) { success, exception ->
                if (!success) {
                    player.sendMessage("${ChatColor.RED}Failed to fetch NPC's texture!")
                }
            }
        }

        if (config.contains("defined-npcs.$id.command")) {
            npc.command = config.getString("defined-npcs.$id.command")
        }

        npc.spawn(player)

        EntityHandler.trackEntity(npc)
        EntityHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully spawned NPC!")
    }

}