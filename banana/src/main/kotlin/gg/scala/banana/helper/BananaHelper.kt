package gg.scala.banana.helper

import gg.scala.banana.Banana

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
object BananaHelper {

    @JvmStatic
    fun handle(lambda: () -> Unit, banana: Banana) {
        if (banana.options.async) {
            banana.options.executor.execute(lambda)
        } else {
            lambda.invoke()
        }
    }
}
