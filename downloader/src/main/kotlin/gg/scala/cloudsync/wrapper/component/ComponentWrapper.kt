package gg.scala.cloudsync.wrapper.component

import gg.scala.cloudsync.wrapper.component.asset.ComponentAssetWrapper
import java.util.*

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
data class ComponentWrapper(
    // internal nexus component id
    val id: String,
    // artifact details
    val group: String,
    val name: String,
    val version: String,
    // modification & creation dates
    // TODO: 1/21/2022 figure out the
    //  actual format that nexus provides us
    val lastModified: Date,
    val blobCreated: Date,
    // actual assets
    val assets: List<ComponentAssetWrapper>
)
