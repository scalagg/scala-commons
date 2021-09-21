package gg.scala.banana.handler.impl

import gg.scala.banana.handler.ExceptionHandler

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
object DefaultExceptionHandler : ExceptionHandler {

    override fun onIncomingPacket(packet: String, exception: Exception) {
        println("Failed to parse incoming packet \"$packet\" due to: ${exception.message}")
    }

    override fun onOutgoingPacket(packet: String, exception: Exception) {
        println("Failed to handle outgoing \"$packet\" due to: ${exception.message}")
    }
}
