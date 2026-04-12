package gg.scala.commons.rassist.dao

import java.util.UUID

data class ReplayMetadata(
    val timestamps: ReplayTimestamp,
    val participants: List<UUID>,
)
