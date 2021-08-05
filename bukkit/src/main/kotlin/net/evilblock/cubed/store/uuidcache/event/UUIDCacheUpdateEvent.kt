package net.evilblock.cubed.store.uuidcache.event

import net.evilblock.cubed.plugin.PluginEvent
import java.util.*

/**
 * This event is called asynchronously.
 */
class UUIDCacheUpdateEvent(val uuid: UUID, val name: String) : PluginEvent()