package gg.scala.commons.sentry.hints

interface SentryHintProvider
{
    fun provideHint(throwable: Throwable): String?
}
