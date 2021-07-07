/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.hook

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import java.util.*

object VaultHook {

    private var economy: Economy? = null
    private var attemptedEconomyHook: Boolean = false

    private var permissions: Permission? = null
    private var attemptedPermissionsHook: Boolean = false

    @JvmStatic
    fun <T> useEconomy(lambda: (Economy) -> T): T {
        setupEconomy()

        if (economy == null) {
            throw IllegalStateException("Failed to hook into VaultAPI economy")
        }

        return lambda.invoke(economy!!)
    }

    @JvmStatic
    fun <T> useEconomyAndReturn(lambda: (Economy) -> T): T {
        setupEconomy()

        if (economy == null) {
            throw IllegalStateException("Failed to hook into VaultAPI economy")
        }

        return lambda.invoke(economy!!)
    }

    @JvmStatic
    fun usePermissions(lambda: (Permission) -> Unit) {
        setupPermissions()

        if (permissions == null) {
            throw IllegalStateException("Failed to hook into VaultAPI permissions")
        }

        return lambda.invoke(permissions!!)
    }

    @JvmStatic
    fun <T> usePermissionsAndReturn(lambda: (Permission) -> T): T {
        setupPermissions()

        if (permissions == null) {
            throw IllegalStateException("Failed to hook into VaultAPI permissions")
        }

        return lambda.invoke(permissions!!)
    }

    @JvmStatic
    fun getBalance(uuid: UUID): Double {
        return useEconomyAndReturn { economy -> economy.getBalance(Bukkit.getOfflinePlayer(uuid)) }
    }

    private fun setupEconomy() {
        if (economy == null && !attemptedEconomyHook) {
            attemptedEconomyHook = true

            try {
                if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
                    throw IllegalStateException("Vault is not enabled!")
                }

                val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
                    ?: throw IllegalStateException("No plugin has registered an Economy service with Vault!")

                this.economy = rsp.provider as Economy
            } catch (e: Exception) {}
        }
    }

    private fun setupPermissions() {
        if (permissions == null && !attemptedPermissionsHook) {
            attemptedPermissionsHook = true

            try {
                if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
                    throw IllegalStateException("Vault is not enabled!")
                }

                val rsp = Bukkit.getServer().servicesManager.getRegistration(Permission::class.java) ?: throw IllegalStateException("No plugin has registered an Economy service with Vault!")
                this.permissions = rsp.provider as Permission
            } catch (e: Exception) {}
        }
    }

}