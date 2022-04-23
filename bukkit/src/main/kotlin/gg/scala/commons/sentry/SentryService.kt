package gg.scala.commons.sentry

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.custom.CustomAnnotationProcessors
import gg.scala.commons.sentry.hints.RegisterHintProvider
import gg.scala.commons.sentry.hints.SentryHintProvider
import io.sentry.Sentry
import io.sentry.protocol.SentryId
import java.util.concurrent.Executors

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
object SentryService
{
    private val hintProviders =
        mutableListOf<SentryHintProvider>()

    var enabled = false

    private val executor = Executors
        .newSingleThreadExecutor()

    fun registerHintProvider(
        provider: SentryHintProvider
    )
    {
        this.hintProviders.add(provider)
    }

    fun log(throwable: Throwable, lambda: (SentryId?) -> Unit)
    {
        if (enabled)
        {
            executor.submit {
                var hint: String? = null

                for (provider in this.hintProviders)
                {
                    val provided = provider
                        .provideHint(throwable)
                        ?: continue

                    hint = provided
                    break
                }

                Sentry.captureException(throwable, hint)
                    .apply(lambda)
            }
        } else
        {
            lambda.invoke(null)
        }
    }

    fun initialize(
        plugin: ExtendedScalaPlugin
    )
    {
        val config = plugin
            .config<SentryConfig>()

        if (!config.enabled)
        {
            plugin.logger.info("[Sentry] Skipping sentry initialization.")
            return
        }

        plugin.logger.info("[Sentry] Initializing sentry...")

        Sentry.init { options ->
            options.dsn = config.dataSourceName

            options.connectionTimeoutMillis = 10000
            options.readTimeoutMillis = 10000

            options.environment = "production"

            enabled = true

            plugin.logger.info("[Sentry] Initialized sentry!")
        }

        CustomAnnotationProcessors
            .process<RegisterHintProvider> {
                this.hintProviders.add(it as SentryHintProvider)
            }
    }
}
