

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

        var ableToApplyGlowEnchant = false
        private var enchantment: Enchantment? = null

        fun registerEnchant() {
            val accepting = Enchantment::class.java
                .getDeclaredField("acceptingNew")

            accepting.isAccessible = true
            accepting.set(null, true)

            enchantment = GlowEnchantment(255)

            if (Enchantment.getByName("Glow") == null) {
                Enchantment.registerEnchantment(enchantment!!)
            }

            ableToApplyGlowEnchant = true
        }

        fun addGlow(item: ItemStack) {
            if (!ableToApplyGlowEnchant)
            {
                return
            }

            if (item.enchantments.isEmpty()) {
                item.addEnchantment(enchantment, 1)
            }
        }
    }
}
