

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
