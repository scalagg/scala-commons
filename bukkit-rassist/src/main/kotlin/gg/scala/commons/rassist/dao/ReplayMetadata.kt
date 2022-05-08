package gg.scala.commons.rassist.dao

import java.util.UUID

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
data class ReplayMetadata(
    val timestamps: ReplayTimestamp,
    val participants: List<UUID>,
)
