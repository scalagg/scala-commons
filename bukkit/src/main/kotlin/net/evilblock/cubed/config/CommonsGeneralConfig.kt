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
    val backups = false

    val ignoreServerCheck = false

    @Comment("Should we enable helper's built-in /helper information command?")
    val infoCommand = true

    @Comment("Mojang, Ashcon")
    var uuidCacheResolverType = "Mojang"
}
