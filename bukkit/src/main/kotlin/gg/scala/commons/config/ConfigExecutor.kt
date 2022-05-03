package gg.scala.commons.config

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
object ConfigExecutor : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
