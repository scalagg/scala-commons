package gg.scala.commons.event

import gg.scala.commons.sentry.SentryService
import net.evilblock.cubed.ScalaCommonsSpigot
import java.util.logging.Level

abstract class StatelessEvent : AbstractEvent<Unit>()
{
    override fun postPushCompute()
    {

    }

    override fun postPushFailure(throwable: Throwable)
    {
        SentryService.push(throwable)

        ScalaCommonsSpigot.instance.logger.log(
            Level.SEVERE, "Exception thrown during event push", throwable
        )
    }
}
