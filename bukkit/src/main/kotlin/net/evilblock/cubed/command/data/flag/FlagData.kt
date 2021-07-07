/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.flag

import net.evilblock.cubed.command.data.Data

data class FlagData(val names: List<String>,
                    val description: String,
                    val defaultValue: Boolean,
                    val methodIndex: Int) : Data