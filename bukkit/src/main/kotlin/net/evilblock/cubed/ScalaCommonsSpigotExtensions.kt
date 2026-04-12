@file:JvmName("ScalaCommonsSpigotExtensions")
package net.evilblock.cubed

import gg.scala.commons.infrastructure.RollingUpdateStartEvent
import org.bukkit.Bukkit

/**
 * @author Limo
 * @date 2/3/26
 */

private var _isAgonesInstanceDraining: Boolean = false

var ScalaCommonsSpigot.isAgonesInstanceDraining: Boolean
    get() = _isAgonesInstanceDraining
    set(value) {
        if (!_isAgonesInstanceDraining && value) {
            _isAgonesInstanceDraining = true
            Bukkit.getPluginManager().callEvent(RollingUpdateStartEvent())
        }
        _isAgonesInstanceDraining = value
    }

fun ScalaCommonsSpigot.markAsDraining()
{
    this.isAgonesInstanceDraining = true
}

fun isAgonesInstanceDraining(): Boolean = ScalaCommonsSpigot.instance.isAgonesInstanceDraining
