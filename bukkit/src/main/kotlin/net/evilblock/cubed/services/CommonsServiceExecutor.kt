package net.evilblock.cubed.services

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object CommonsServiceExecutor : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor(
        ThreadFactoryBuilder()
            .setNameFormat("commons-serviceexecutor-thread-%d")
            .setDaemon(true)
            .build()
    )
