package gg.scala.banana.handler

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
interface ExceptionHandler {

    fun onIncomingPacket(packet: String, exception: Exception)
    fun onOutgoingPacket(packet: String, exception: Exception)

}
