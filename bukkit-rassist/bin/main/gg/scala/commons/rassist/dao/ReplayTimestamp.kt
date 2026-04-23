package gg.scala.commons.rassist.dao

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
data class ReplayTimestamp(
    var beginning: Long,
    var ending: Long = 0L
)
{
    val duration: Long
        get() = ending - beginning
}
