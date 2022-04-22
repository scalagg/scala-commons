package gg.scala.commons.scripts.config

import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
class ScriptConfig
{
    @Comment("How often to poll for script changes.")
    val pollInterval = 20

    @Comment("The time unit to poll at.")
    val pollIntervalTimeUnit = "SECONDS"

    @Comment("The file name of the init script.")
    val initScript = "init.js"
}
