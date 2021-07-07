/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.method

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.CommandHandler
import net.evilblock.cubed.command.CommandNode
import net.evilblock.cubed.command.data.Data
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.flag.FlagData
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.command.data.parameter.ParameterData
import net.evilblock.cubed.command.data.processor.Processor
import org.bukkit.command.CommandSender
import java.lang.IllegalArgumentException
import java.lang.reflect.Method

object MethodProcessor : Processor<Method, Set<CommandNode>?> {

    override fun process(method: Method): Set<CommandNode>? {
        if (method.isAnnotationPresent(Command::class.java)) {
            if (method.parameterCount >= 1 && CommandSender::class.java.isAssignableFrom(method.parameterTypes[0])) {
                val command = method.getAnnotation(Command::class.java)
                val owningClass = method.declaringClass
                val flagNames = mutableListOf<String>()
                val allParams = mutableListOf<Data>()

                if (method.parameterCount > 1) {
                    for (i in 1 until method.parameterCount) {
                        val parameter = method.parameters[i]

                        if (parameter.isAnnotationPresent(Param::class.java)) {
                            val param: Param = parameter.getAnnotation(Param::class.java)
                            val hash = setOf(*param.tabCompleteFlags)

                            val data = ParameterData(
                                param.name,
                                param.defaultValue,
                                parameter.type,
                                param.wildcard,
                                i,
                                hash,
                                CommandHandler.parameterTypeMap[parameter.type]?.javaClass
                            )

                            allParams.add(data)
                        } else {
                            if (!parameter.isAnnotationPresent(Flag::class.java)) {
                                throw IllegalArgumentException("Every data, other than the sender, must be annotated with Param")
                            }

                            val flag: Flag = parameter.getAnnotation(Flag::class.java)
                            val flagData = FlagData(listOf(*flag.value), flag.description, flag.defaultValue, i)

                            allParams.add(flagData)
                            flagNames.addAll(listOf(*flag.value))
                        }
                    }
                }

                return CommandHandler.compileAndRegisterNodes(
                    names = command.names,
                    description = command.description,
                    permission = command.permission,
                    async = command.async,
                    hidden = command.hidden,
                    flags = flagNames,
                    parameters = allParams,
                    logToConsole = command.logToConsole,
                    owningClass = owningClass,
                    owningInstance = null,
                    method = method
                )
            }
        }

        return null
    }

}