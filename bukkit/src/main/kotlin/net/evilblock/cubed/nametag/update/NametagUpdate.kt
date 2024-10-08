package net.evilblock.cubed.nametag.update

import org.bukkit.entity.Player
import java.util.UUID

internal class NametagUpdate(var toRefresh: UUID, var refreshFor: UUID?) {

    constructor(toRefresh: Player): this(toRefresh.uniqueId, null)
    constructor(toRefresh: Player, refreshFor: Player): this(toRefresh.uniqueId, refreshFor.uniqueId)

}
