/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.bukkit

import java.util.Collections
import net.evilblock.cubed.command.CommandNode
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandException
import org.bukkit.entity.Player
import java.util.ArrayList
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.util.StringUtil

class ExtendedCommandMap(server: Server) : SimpleCommandMap(server) {

    override fun tabComplete(sender: CommandSender, cmdLine: String): List<String>? {
        checkNotNull(sender as Any) { "Sender cannot be null" }
        checkNotNull(cmdLine as Any) { "Command line cannot be null" }

        val spaceIndex = cmdLine.indexOf(' ')

        if (spaceIndex == -1) {
            val completions = ArrayList<String>()
            val knownCommands = knownCommands as Map<String, Command>
            val prefix = if (sender is Player) "/" else ""

            for ((name, command) in knownCommands) {
                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    if (command is ExtendedCommand) {
                        var executionNode = command.node.getCommand(name)

                        if (executionNode == null) {
                            executionNode = command.node
                        }

                        if (!executionNode.hasCommands()) {
                            var testNode: CommandNode? = executionNode.getCommand(name)

                            if (testNode == null) {
                                testNode = command.node.getCommand(name)
                            }

                            if (!testNode!!.canUse(sender)) {
                                continue
                            }

                            completions.add(prefix + name)
                        } else {
                            if (executionNode.getSubCommands(sender, false).isEmpty()) {
                                continue
                            }

                            completions.add(prefix + name)
                        }
                    } else {
                        if (!command.testPermissionSilent(sender)) {
                            continue
                        }

                        completions.add(prefix + name)
                    }
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER)
            return completions
        }

        val commandName = cmdLine.substring(0, spaceIndex)
        val target = getCommand(commandName) ?: return null

        if (!target.testPermissionSilent(sender)) {
            return null
        }

        val argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length)
        val args = argLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            val completions = if (target is ExtendedCommand) target.tabComplete(sender, cmdLine) else target.tabComplete(sender, commandName, args)

            if (completions != null) {
                return if (completions !is MutableList) {
                    completions.sortedWith(String.CASE_INSENSITIVE_ORDER)
                } else {
                    completions.toMutableList().sortedWith(String.CASE_INSENSITIVE_ORDER)
                }
            }

            return completions
        } catch (ex: CommandException) {
            throw ex
        } catch (ex2: Throwable) {
            throw CommandException("Unhandled exception executing tab-completer for '$cmdLine' in $target", ex2)
        }

    }

}