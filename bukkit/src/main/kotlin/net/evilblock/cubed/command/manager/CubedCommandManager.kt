package net.evilblock.cubed.command.manager

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import net.evilblock.cubed.CubedConfig
import net.evilblock.cubed.command.*
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.command.HologramCommand
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.command.NpcCommand
import net.evilblock.cubed.entity.npc.texture.NpcTexture
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.uuid.UUIDUtil
import net.evilblock.cubed.util.time.Duration
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author puugz
 * @since 10/08/2021 20:38
 */
class CubedCommandManager(plugin: Plugin) : PaperCommandManager(plugin) {

    init {
        enableUnstableAPI("help")
        commandContexts.registerContext(NpcEntity::class.java) { c ->
            val entity: Entity = try {
                EntityHandler.getEntityById(c.firstArg.toInt())
            } catch (e: Exception) {
                try {
                    EntityHandler.getEntityByUuid(UUID.fromString(c.firstArg))
                } catch (e: Exception) {
                    throw ConditionFailedException("No NPC by id matching ${CC.YELLOW}${c.firstArg} ${CC.RED}was found.")
                }
            } ?: throw ConditionFailedException("No NPC was found.")

            return@registerContext entity as NpcEntity?
        }
        commandContexts.registerIssuerOnlyContext(ConsoleCommandSender::class.java) { c ->
            if (c.sender !is ConsoleCommandSender) {
                throw ConditionFailedException("Only console may perform this command.")
            }
            return@registerIssuerOnlyContext c.sender as ConsoleCommandSender
        }
        commandContexts.registerContext(Entity::class.java) { c ->
            return@registerContext try {
                EntityHandler.getEntityByUuid(UUID.fromString(c.firstArg))
            } catch (e: Exception) {
                try {
                    EntityHandler.getEntityById(c.firstArg.toInt())
                } catch (e: Exception) {
                    throw ConditionFailedException("No entity by id matching ${CC.YELLOW}${c.firstArg} ${CC.RED}was found.")
                }
            } ?: throw ConditionFailedException("No entity was found.")
        }
        commandContexts.registerContext(NpcTexture::class.java) { c ->
            return@registerContext if (CubedConfig.hasNpcTexture(c.firstArg)) {
                CubedConfig.getNpcTexture(c.firstArg)
            } else {
                c.sender.sendMessage("${CC.GREEN}Attempting to fetch player texture...")

                val queryResponse =
                    CompletableFuture.supplyAsync { NpcTexture.fetchTextureByUsername(c.firstArg) }.get()

                if (queryResponse.isPresent) {
                    queryResponse.get()
                } else {
                    throw ConditionFailedException("Failed to fetch player texture for ${CC.YELLOW}${c.firstArg}${CC.RED}.")
                }
            }
        }
        commandCompletions.registerAsyncCompletion("npctextures") { c ->
            return@registerAsyncCompletion CubedConfig.getNpcTextureIds().toList()
        }
        commandContexts.registerContext(MenuTemplate::class.java) { c ->
            return@registerContext try {
                MenuTemplateHandler.findTemplate(c.firstArg)
            } catch (ignored: Exception) {
                throw ConditionFailedException("No menu template matching ${CC.YELLOW}${c.firstArg}${CC.RED} found.")
            }
        }
        commandCompletions.registerAsyncCompletion("menutemplates") { c ->
            val completions = mutableListOf<String>()

            MenuTemplateHandler.getTemplates().forEach {
                completions.add(it.getName())
            }

            return@registerAsyncCompletion completions
        }
        commandContexts.registerContext(Duration::class.java) { c ->
            return@registerContext try {
                Duration.parse(c.firstArg)
            } catch (ignored: Exception) {
                throw ConditionFailedException("Invalid duration parsed. (Example: 1h30m)")
            }
        }
        commandContexts.registerContext(UUID::class.java) { c ->
            if (c.firstArg.length != 32 || c.firstArg.length != 36) {
                throw ConditionFailedException(CC.YELLOW + c.firstArg + CC.RED + " is not a valid uuid.")
            }

            if (c.firstArg.length == 32) {
                return@registerContext UUIDUtil.formatUUID(c.firstArg)
                    ?: throw ConditionFailedException(CC.YELLOW + c.firstArg + CC.RED + " is not a valid uuid.")
            }

            return@registerContext try {
                UUID.fromString(c.firstArg)
            } catch (ignored: Exception) {
                throw ConditionFailedException(CC.YELLOW + c.firstArg + CC.RED + " is not a valid uuid.")
            }
        }
    }
}
