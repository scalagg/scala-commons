package gg.scala.commons.rassist.recorder

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object RecorderBatchExecutor : ScheduledExecutorService
    by Executors.newScheduledThreadPool(3)
