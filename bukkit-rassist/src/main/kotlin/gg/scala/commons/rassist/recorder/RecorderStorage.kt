package gg.scala.commons.rassist.recorder

import java.util.UUID

object RecorderStorage
{
    val recorders =
        mutableMapOf<UUID, Recorder>()
}
