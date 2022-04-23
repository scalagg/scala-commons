package gg.scala.commons.sentry.hints

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
interface SentryHintProvider
{
    fun provideHint(throwable: Throwable): String?
}
