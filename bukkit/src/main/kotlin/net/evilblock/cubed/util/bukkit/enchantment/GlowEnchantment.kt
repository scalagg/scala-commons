/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit.enchantment

import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.inventory.ItemStack

class GlowEnchantment(id: Int) : EnchantmentWrapper(id) {

    override fun canEnchantItem(item: ItemStack): Boolean {
        return true
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        return false
    }

    override fun getItemTarget(): EnchantmentTarget? {
        return null
    }

    override fun getMaxLevel(): Int {
        return 10
    }

    override fun getName(): String {
        return "Glow"
    }

    override fun getStartLevel(): Int {
        return 1
    }

    companion object {

        private var glow: Enchantment? = null

        fun registerEnchant() {
            try {
                val f = Enchantment::class.java.getDeclaredField("acceptingNew")
                f.isAccessible = true
                f.set(null, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            glow = GlowEnchantment(255)

            if (Enchantment.getByName("Glow") == null) {
                Enchantment.registerEnchantment(glow!!)
            }
        }

        fun addGlow(item: ItemStack) {
            if (item.enchantments.isEmpty()) {
                item.addEnchantment(glow, 1)
            } else {
                return
            }
        }

    }

}