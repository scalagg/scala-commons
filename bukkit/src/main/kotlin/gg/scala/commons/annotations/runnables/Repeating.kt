package gg.scala.commons.annotations.runnables

import me.lucko.helper.promise.ThreadContext

@Target(
    AnnotationTarget.CLASS
)
annotation class Repeating(
    val value: Long,
    val delay: Long = 0L,
    val context: ThreadContext =
        ThreadContext.SYNC
)
