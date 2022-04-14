/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.protocol

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.util.Reflection
import org.bukkit.Material

object MenuCompatibility
{
    @JvmStatic
    fun getBarrierOrReplacement(): XMaterial
    {
        return XMaterial.BARRIER
    }
}
