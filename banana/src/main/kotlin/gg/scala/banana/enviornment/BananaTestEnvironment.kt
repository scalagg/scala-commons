package gg.scala.banana.enviornment

import gg.scala.banana.BananaBuilder
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
object BananaTestEnvironment {

    @JvmStatic
    fun main(args: Array<String>) {
        val reader = BufferedReader(
            InputStreamReader(System.`in`)
        )

        val banana = BananaBuilder()
            .options(
                BananaOptions(
                    channel = "haha",
                    async = true
                )
            )
            .credentials(
                BananaCredentials()
            )
            .build()

        banana.registerClass(BananaTestEnvironment)
        banana.subscribe()

        while (true) {
            val message = Message("some-packet")
            message["something"] = reader.readLine()

            message.dispatch()
        }
    }

    @Subscribe("some-packet", priority = 44)
    fun onHi(message: Message) {
        println("received some packet! Here's the message: ${message["something"]}")
    }
}
