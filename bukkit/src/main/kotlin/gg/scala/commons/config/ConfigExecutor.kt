package gg.scala.commons.config

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object ConfigExecutor : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
