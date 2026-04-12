package gg.scala.commons.persist.datasync

import java.util.concurrent.CopyOnWriteArrayList

private val reloadCallbacks = mutableMapOf<Any, CopyOnWriteArrayList<() -> Unit>>()

/**
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
