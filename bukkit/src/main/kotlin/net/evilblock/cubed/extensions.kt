package net.evilblock.cubed

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Material

/**
 * @author GrowlyX
 * @since 8/23/2022
 */
fun Material.x() = XMaterial.matchXMaterial(this)
