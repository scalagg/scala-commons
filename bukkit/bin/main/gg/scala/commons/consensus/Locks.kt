package gg.scala.commons.consensus

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Provides a simple in-process locking mechanism
 * scoped by namespace and key, ensuring that concurrent
 * operations on the same resource are serialized.
 *
 * @author Limo
 * @date 2/10/2026
 */
object Locks
{
    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    /**
     * Acquires a lock scoped to the given namespace and key,
     * executes the provided action, and returns the result
     * wrapped in a CompletableFuture.
     */
    fun <T> withGlobalLock(
        namespace: String,
        key: String,
        action: () -> T
    ): CompletableFuture<T>
    {
        return CompletableFuture.supplyAsync {
            val lockKey = "$namespace:$key"
            val lock = locks.computeIfAbsent(lockKey) { ReentrantLock() }

            lock.withLock {
                action()
            }
        }
    }
}
