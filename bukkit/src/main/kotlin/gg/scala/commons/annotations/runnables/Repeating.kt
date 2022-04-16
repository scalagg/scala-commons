package gg.scala.commons.annotations.runnables

import me.lucko.helper.promise.ThreadContext
import org.atteo.classindex.IndexAnnotated

/**
 * @author GrowlyX
 * @since 4/14/2022
 */
@Target(
    AnnotationTarget.CLASS
)
@IndexAnnotated
annotation class Repeating(
    val value: Long,
    val delay: Long = 0L,
    val context: ThreadContext =
        ThreadContext.SYNC
)
