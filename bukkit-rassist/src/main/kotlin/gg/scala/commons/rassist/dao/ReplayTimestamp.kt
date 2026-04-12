package gg.scala.commons.rassist.dao

data class ReplayTimestamp(
    var beginning: Long,
    var ending: Long = 0L
)
{
    val duration: Long
        get() = ending - beginning
}
