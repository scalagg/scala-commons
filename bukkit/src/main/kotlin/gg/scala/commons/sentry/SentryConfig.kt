package gg.scala.commons.sentry

import xyz.mkotb.configapi.comment.Comment

class SentryConfig
{
    @Comment("Should we report exceptions to Sentry?")
    val enabled = false

    val dataSourceName = "https://examplePublicKey@o0.ingest.sentry.io/0"
}
