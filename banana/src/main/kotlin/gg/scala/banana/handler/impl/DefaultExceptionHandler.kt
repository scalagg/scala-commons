package gg.scala.banana.handler.impl

import gg.scala.banana.handler.ExceptionHandler

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
object DefaultExceptionHandler : ExceptionHandler {

    override fun onIncomingPacket(packet: String, exception: Exception) {
        println("[Banana] Failed to parse incoming packet \"$packet\" due to: ${exception.message}")
        exception.printStackTrace()
    }

    override fun onOutgoingPacket(packet: String, exception: Exception) {
        println("[Banana] Failed to handle outgoing \"$packet\" due to: ${exception.message}")
        exception.printStackTrace()
    }
}
