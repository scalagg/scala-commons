package gg.scala.banana.credentials

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
class BananaCredentials(
    val address: String = "localhost",
    val port: Int = 6379,
    val authenticate: Boolean = false,
    val password: String = ""
)
