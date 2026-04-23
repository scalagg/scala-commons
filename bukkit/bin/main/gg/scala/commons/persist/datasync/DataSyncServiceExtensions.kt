package gg.scala.commons.persist.datasync

import java.util.concurrent.CopyOnWriteArrayList

private val reloadCallbacks = mutableMapOf<Any, CopyOnWriteArrayList<() -> Unit>>()

/**
 * Extension function to register a callback that fires when the service reloads.
 *
 * @author Limo
 * @date 2/5/2026
 */
fun <T : DataSyncService<*>> T.onReload(callback: () -> Unit)
{
    reloadCallbacks
        .getOrPut(this) { CopyOnWriteArrayList() }
        .add(callback)
}

/**
 * Triggers all registered reload callbacks for a service.
 * Call this from postReload() in DataSyncService subclasses.
 *
 * @author Limo
 * @date 2/5/2026
 */
fun <T : DataSyncService<*>> T.fireReloadCallbacks()
{
    reloadCallbacks[this]?.forEach { callback ->
        kotlin.runCatching { callback() }
            .onFailure { it.printStackTrace() }
    }
}
