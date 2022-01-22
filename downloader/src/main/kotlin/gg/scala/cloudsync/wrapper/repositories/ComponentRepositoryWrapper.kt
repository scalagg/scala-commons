package gg.scala.cloudsync.wrapper.repositories

import gg.scala.cloudsync.wrapper.RepositoryWrapper
import gg.scala.cloudsync.wrapper.component.ComponentWrapper

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
class ComponentRepositoryWrapper(
    override val items: MutableList<ComponentWrapper> = mutableListOf(),
    val continuationToken: String? = null
) : RepositoryWrapper<ComponentWrapper>
