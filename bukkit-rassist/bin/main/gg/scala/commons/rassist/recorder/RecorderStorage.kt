package gg.scala.commons.rassist.recorder

import java.util.UUID

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
object RecorderStorage
{
    val recorders =
        mutableMapOf<UUID, Recorder>()
}
