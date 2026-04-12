package net.evilblock.cubed

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Material

fun Material.x() = XMaterial.matchXMaterial(this)
