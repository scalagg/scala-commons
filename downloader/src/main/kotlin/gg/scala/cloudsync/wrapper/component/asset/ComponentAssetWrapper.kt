package gg.scala.cloudsync.wrapper.component.asset

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
class ComponentAssetWrapper(
    val downloadUrl: String,
    val format: String,
    // maven2 wrapper - could be nullable?
    val maven2: ComponentAssetWrapperMaven2?,
    val checksum: ComponentAssetWrapperChecksum
)
