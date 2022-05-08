package gg.scala.commons.rassist.recorder

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
object RecorderBatchExecutor : ScheduledExecutorService
    by Executors.newScheduledThreadPool(3)
