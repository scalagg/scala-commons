package net.evilblock.cubed.config

import xyz.mkotb.configapi.Coloured
import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 4/27/2022
 */
class CommonsNametagConfig
{
    @Comment("Should we enable our nametag providers?")
    val enabled = true

    @Comment("Should we restrict outgoing packets?")
    val packetRestriction = false

    @Coloured
    val packetRestrictionPrefix = ""

    val unweightedTab = false
    val pushAsyncUpdates = true
    val asyncUpdatesIntervalTicks = 2
}
