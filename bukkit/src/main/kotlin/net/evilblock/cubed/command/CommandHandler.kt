/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

import co.aikar.timings.TimingsManager
import net.evilblock.cubed.command.bukkit.ExtendedCommand
import net.evilblock.cubed.command.bukkit.ExtendedCommandMap
import net.evilblock.cubed.command.bukkit.ExtendedHelpTopic
import net.evilblock.cubed.command.data.Data
import net.evilblock.cubed.command.data.method.MethodProcessor
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.command.data.parameter.impl.*
import net.evilblock.cubed.util.ClassUtils
import net.evilblock.cubed.util.bukkit.PluginHelper
import net.evilblock.cubed.util.bukkit.player.OfflinePlayerWrapper
import net.evilblock.cubed.util.time.Duration
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.Plugin
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object CommandHandler {

    val rootNode: CommandNode = CommandNode()

    val parameterTypeMap: MutableMap<Class<*>, ParameterType<*>> = HashMap()
    private val commandMap: CommandMap
    private val knownCommands: MutableMap<String, Command>

    init {
        val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true

        commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

        val knownCommandsField = commandMap.javaClass.getDeclaredField("knownCommands")
        knownCommandsField.isAccessible = true

        knownCommands = knownCommandsField.get(commandMap) as MutableMap<String, Command>
    }

    fun load() {
        registerParameterType(Boolean::class.java, BooleanParameterType())
        registerParameterType(Integer::class.java, IntegerParameterType())
        registerParameterType(Int::class.java, IntegerParameterType())
        registerParameterType(Double::class.java, DoubleParameterType())
        registerParameterType(Long::class.java, LongParameterType())
        registerParameterType(Float::class.java, FloatParameterType())
        registerParameterType(String::class.java, StringParameterType())
        registerParameterType(ChatColor::class.java, ChatColorParameterType())
        registerParameterType(Enchantment::class.java, EnchantmentParameterType())
        registerParameterType(ItemFlag::class.java, ItemFlagParameterType())

        val equipmentSlotClass = try {
            Class.forName("org.bukkit.inventory.EquipmentSlot")
        } catch (e: Exception) {
            null
        }

        if (equipmentSlotClass != null) {
            registerParameterType(equipmentSlotClass, EquipmentSlotParameterType())
        }

        registerParameterType(GameMode::class.java, GameModeParameterType())
        registerParameterType(Player::class.java, PlayerParameterType())
        registerParameterType(World::class.java, WorldParameterType())
        registerParameterType(OfflinePlayer::class.java, OfflinePlayerParameterType())
        registerParameterType(OfflinePlayerWrapper::class.java, OfflinePlayerWrapperParameterType())
        registerParameterType(UUID::class.java, UUIDParameterType())
        registerParameterType(Duration::class.java, Duration.DurationParameterType())

        swapCommandMap()
    }

    fun registerParameterType(clazz: Class<*>, parameterType: ParameterType<*>) {
        parameterTypeMap[clazz] = parameterType
    }

    fun getParameterType(clazz: Class<*>): ParameterType<*>? {
        return parameterTypeMap[clazz]
    }

    private fun swapCommandMap() {
        val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true

        if (commandMapField.get(Bukkit.getServer()) is ExtendedCommandMap) {

        }

        val oldCommandMap = commandMapField.get(Bukkit.getServer())
        val newCommandMap = ExtendedCommandMap(Bukkit.getServer())

        val knownCommandsField = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
        knownCommandsField.isAccessible = true

        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(knownCommandsField, knownCommandsField.modifiers and -0x11)

        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap))
        commandMapField.set(Bukkit.getServer(), newCommandMap)
    }

    fun registerClass(clazz: Class<*>) {
        for (method in clazz.methods) {
            registerMethod(method)
        }
    }

    private fun registerMethod(method: Method) {
        method.isAccessible = true

        MethodProcessor.process(method)?.forEach { node ->
            val command = ExtendedCommand(node, PluginHelper.getProvidingPlugin(method.declaringClass))

            register(command)

            node.children.values.forEach { child ->
                registerHelpTopic(child, node.aliases)
            }
        }
    }

    private fun register(command: ExtendedCommand) {
        val iterator = knownCommands.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.name.equals(command.name, true)) {
                entry.value.unregister(commandMap)
                iterator.remove()
            }
        }

        command.timings = TimingsManager.getCommandTiming("Cubed", command) // Spigot

        command.aliases.forEach { alias ->
            knownCommands[alias] = command
        }

        command.register(commandMap)
        knownCommands[command.name] = command
    }

    private fun registerHelpTopic(node: CommandNode, aliases: Set<String>?) {
        if (node.method != null) {
            Bukkit.getHelpMap().addTopic(ExtendedHelpTopic(node, aliases))
        }

        if (node.hasCommands()) {
            node.children.values.forEach { child ->
                registerHelpTopic(child, null)
            }
        }
    }

    fun registerPackage(plugin: Plugin, packageName: String) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(this::registerClass)
    }

    fun registerAll(plugin: Plugin) {
        registerPackage(plugin, plugin::class.java.`package`.name)
    }

    fun registerExtended(command: ICommand) {
        val nodes = compileAndRegisterNodes(
            names = command.getNames(),
            description = command.getDescription(),
            permission = command.getPermission(),
            async = command.isAsync(),
            hidden = command.isHidden(),
            logToConsole = command.isLogToConsole(),
            flags = arrayListOf(),
            parameters = arrayListOf(),
            owningClass = command::class.java.declaringClass,
            owningInstance = null,
            method = command.getMethod()
        )

        val node = CommandNode(command::class.java)

        node.description = command.getDescription()
        node.permission = command.getPermission()
        command.isAsync()
        command.isHidden()

        val command = ExtendedCommand(node, PluginHelper.getProvidingPlugin(command::class.java.declaringClass))

        register(command)

        node.children.values.forEach { child ->
            registerHelpTopic(child, node.aliases)
        }
    }

    fun compileAndRegisterNodes(names: Array<String>,
                                description: String,
                                permission: String,
                                async: Boolean,
                                hidden: Boolean,
                                logToConsole: Boolean,
                                flags: MutableList<String>,
                                parameters: MutableList<Data>,
                                owningClass: Class<*>,
                                owningInstance: Any?,
                                method: Method
    ): HashSet<CommandNode> {
        val registered = HashSet<CommandNode>()

        for (name in names) {
            val qualifiedName = name.toLowerCase().trim()
            var hadChild = false
            var cmdNames: Array<String> = arrayOf(qualifiedName)

            if (qualifiedName.contains(" ")) {
                cmdNames = qualifiedName.split(" ").toTypedArray()
            }

            val primaryName = cmdNames[0]
            var workingNode = CommandNode(owningClass)

            if (rootNode.hasCommand(primaryName)) {
                workingNode = rootNode.getCommand(primaryName)!!
                workingNode.aliases.add(primaryName)
            } else {
                workingNode.name = primaryName
            }

            var parentNode = CommandNode(owningClass)

            if (workingNode.hasCommand(primaryName)) {
                parentNode = workingNode.getCommand(primaryName)!!
            } else {
                parentNode.name = primaryName
                parentNode.permission = ""
            }

            if (cmdNames.size > 1) {
                hadChild = true

                workingNode.registerCommand(parentNode)

                var childNode = CommandNode(owningClass)

                for (i in 1 until cmdNames.size) {
                    val subName = cmdNames[i]

                    childNode.name = subName

                    if (parentNode.hasCommand(subName)) {
                        childNode = parentNode.getCommand(subName)!!
                    }

                    parentNode.registerCommand(childNode)

                    if (i == cmdNames.size - 1) {
                        childNode.method = method
                        childNode.async = async
                        childNode.hidden = hidden
                        childNode.permission = permission
                        childNode.description = description
                        childNode.validFlags = flags
                        childNode.parameters = parameters
                        childNode.logToConsole = logToConsole
                        childNode.owningInstance = owningInstance
                    } else {
                        parentNode = childNode
                        childNode = CommandNode(owningClass)
                    }
                }
            }

            if (!hadChild) {
                parentNode.method = method
                parentNode.async = async
                parentNode.hidden = hidden
                parentNode.permission = permission
                parentNode.description = description
                parentNode.validFlags = flags
                parentNode.parameters = parameters
                parentNode.owningInstance = owningInstance
                parentNode.logToConsole = logToConsole

                workingNode.registerCommand(parentNode)
            }

            rootNode.registerCommand(workingNode)
            registered.add(workingNode)
        }

        return registered
    }

}