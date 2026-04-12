package gg.scala.commons.consensus

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author Limo
 * @date 2/10/2026
 */
object Locks
{
    private val locks = ConcurrentHashMap<String, ReentrantLock>()

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
