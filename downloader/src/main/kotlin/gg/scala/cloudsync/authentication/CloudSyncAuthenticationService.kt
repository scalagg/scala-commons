package gg.scala.cloudsync.authentication

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import xyz.mkotb.configapi.ConfigFactory
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 50)
object CloudSyncAuthenticationService
{
    @Inject
    lateinit var factory: ConfigFactory

    private var authentication by Delegates
        .notNull<ScalaConnectionAuthenticationWrapper>()

    @Configure
    fun configure()
    {
        authentication = factory.fromFile(
            "authentication",
            ScalaConnectionAuthenticationWrapper::class.java
        )
    }

    fun getWrapper() = authentication

    class ScalaConnectionAuthenticationWrapper(
        val username: String = "admin",
        val password: String = "password"
    )
}
