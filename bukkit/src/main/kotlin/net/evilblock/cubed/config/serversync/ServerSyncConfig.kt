package net.evilblock.cubed.config.serversync

import gg.scala.commons.agnostic.sync.server.region.Region
import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
@Comment(
    "This is the primary config for our ServerSync software.",
    "We recommend editing this file to prevent conflicts with other servers."
)
class ServerSyncConfig
{
    @Comment(
        "Whether we enable serversync or not.",
        "* Ensure no plugins make use of this functionality before disabling it!"
    )
    val enabled = true

    @Comment("This is your unique server ID.")
    val serverId = "na-server-1"

    @Comment("This is a list of server groups.")
    val serverGroups = listOf(
        "servers", "example"
    )

    @Comment(
        "This is the region this server is located in.",
        "Options: NA, EU, SA, AS, AU, AF, ME."
    )
    val region = "NA"
}
