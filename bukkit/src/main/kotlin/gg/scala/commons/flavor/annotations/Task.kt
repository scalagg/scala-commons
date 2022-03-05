package gg.scala.commons.flavor.annotations

import me.lucko.helper.promise.ThreadContext
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 3/5/2022
 */
annotation class Task(
    val delay: Long,
    val unit: TimeUnit,
    val ctx: ThreadContext =
        ThreadContext.SYNC
)
