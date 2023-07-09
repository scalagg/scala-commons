package net.evilblock.cubed.config

import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 4/27/2022
 */
@Comment(
    "You're viewing the primary config for scala-commons."
)
class CommonsGeneralConfig
{
    val debug = false
    val timezone = "EST"
    val enable1Dot7Entities = true
    val enableUnifiedMetrics = false
    val ignoreServerCheck = false
    val doNotSaveEntities = false
    val enableEntityPacketAdapters = true

    @Comment("Should we enable helper's built-in /helper information command?")
    val infoCommand = false

    @Comment("Mojang, Ashcon")
    var uuidCacheResolverType = "Mojang"
}
