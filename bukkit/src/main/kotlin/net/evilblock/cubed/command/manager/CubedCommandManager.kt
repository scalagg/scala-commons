package net.evilblock.cubed.command.manager

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.ExceptionHandler
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import gg.scala.cache.uuid.ScalaStoreUuidCache
import net.evilblock.cubed.CubedConfig
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.texture.NpcTexture
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.uuid.UUIDUtil
import net.evilblock.cubed.util.time.Duration
import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.Plugin
import java.util.*

/**
 * @author GrowlyX, puugz
 * @since 10/08/2021 20:38
 */
class CubedCommandManager(
    plugin: Plugin,
    primary: ChatColor = ChatColor.GOLD,
    secondary: ChatColor = ChatColor.YELLOW
) : PaperCommandManager(plugin)
{
    init
    {
        enableUnstableAPI("help")
        enableUnstableAPI("brigadier")

        listOf<MessageType>(
            MessageType.HELP,
            MessageType.INFO,
            MessageType.SYNTAX
        ).forEach {
            getFormat(it).setColor(3, ChatColor.GRAY)
            getFormat(it).setColor(2, secondary)
            getFormat(it).setColor(1, primary)
        }

        defaultExceptionHandler = ExceptionHandler { _, _, sender, _, _ ->
            sender.sendMessage("${
                ""
            }${CC.RED}Something went terribly wrong while trying to perform this command.")
            return@ExceptionHandler true
        }

        commandContexts.registerContext(NpcEntity::class.java) { c ->
            val firstArg = c.popFirstArg()

            val entity: Entity = try
            {
                EntityHandler.getEntityById(firstArg.toInt())
            } catch (e: Exception)
            {
                try
                {
                    EntityHandler.getEntityByUuid(UUID.fromString(firstArg))
                } catch (e: Exception)
                {
                    throw ConditionFailedException("No NPC by id matching ${CC.YELLOW}${firstArg} ${CC.RED}was found.")
                }
            } ?: throw ConditionFailedException("No NPC was found.")

            return@registerContext entity as NpcEntity?
        }

        commandContexts.registerIssuerOnlyContext(ConsoleCommandSender::class.java) { c ->
            if (c.sender !is ConsoleCommandSender)
            {
                throw ConditionFailedException("Only console may perform this command.")
            }
            return@registerIssuerOnlyContext c.sender as ConsoleCommandSender
        }

        commandContexts.registerContext(Entity::class.java) { c ->
            val firstArg = c.popFirstArg()

            return@registerContext try
            {
                EntityHandler.getEntityByUuid(UUID.fromString(firstArg))
            } catch (e: Exception)
            {
                try
                {
                    EntityHandler.getEntityById(firstArg.toInt())
                } catch (e: Exception)
                {
                    throw ConditionFailedException("No entity by id matching ${CC.YELLOW}${firstArg} ${CC.RED}was found.")
                }
            } ?: throw ConditionFailedException("No entity was found.")
        }

        commandContexts.registerContext(NpcTexture::class.java) { c ->
            val firstArg = c.popFirstArg()

            return@registerContext if (CubedConfig.hasNpcTexture(firstArg))
            {
                CubedConfig.getNpcTexture(firstArg)
            } else
            {
                c.sender.sendMessage("${CC.GREEN}Attempting to fetch player texture...")

                val queryResponse = NpcTexture.fetchTextureByUsername(firstArg)

                if (queryResponse.isPresent)
                {
                    queryResponse.get()
                } else
                {
                    throw ConditionFailedException("Failed to fetch player texture for ${CC.YELLOW}${firstArg}${CC.RED}.")
                }
            }
        }

        commandCompletions.registerAsyncCompletion("npctextures") { c ->
            return@registerAsyncCompletion CubedConfig.getNpcTextureIds().toList()
        }

        commandContexts.registerContext(MenuTemplate::class.java) { c ->
            val firstArg = c.popFirstArg()

            return@registerContext try
            {
                MenuTemplateHandler.findTemplate(firstArg)
            } catch (ignored: Exception)
            {
                throw ConditionFailedException("No menu template matching ${CC.YELLOW}${firstArg}${CC.RED} found.")
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
            val firstArg = c.popFirstArg()

            return@registerContext try
            {
                Duration.parse(firstArg)
            } catch (ignored: Exception)
            {
                throw ConditionFailedException("That's an invalid duration. Example: ${CC.BOLD}1h30m")
            }
        }

        commandContexts.registerContext(UUID::class.java) { c ->
            val firstArg = c.popFirstArg()

            if (firstArg.length == 32)
            {
                return@registerContext UUIDUtil.formatUUID(firstArg)
                    ?: throw ConditionFailedException("${CC.YELLOW}${firstArg}${CC.RED} is not a valid uuid.")
            } else if (firstArg.length <= 16)
            {
                return@registerContext ScalaStoreUuidCache.uniqueId(firstArg)
                    ?: throw ConditionFailedException("No player with the username ${CC.YELLOW}${firstArg}${CC.RED} exists.")
            }

            return@registerContext try
            {
                UUID.fromString(firstArg)
            } catch (ignored: Exception)
            {
                throw ConditionFailedException("${CC.YELLOW}${firstArg}${CC.RED} is not a valid uuid.")
            }
        }
    }
}
