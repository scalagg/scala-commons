package gg.scala.banana

import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.BananaSubscription
import gg.scala.banana.subscribe.BananaSubscriptionData
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub
import java.lang.reflect.Method
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Handles everything banana-related
 *
 * @author GrowlyX
 * @since 9/19/2021
 */
data class Banana(
    private val credentials: BananaCredentials,
    val options: BananaOptions,
) {
    private val jedisConfig = JedisPoolConfig()

    private val subscribeJedisPool: JedisPool
    private var publishJedisPool: JedisPool

    private lateinit var pubSub: JedisPubSub

    private var shutdownHookAdded = false

    private val cachedSubExecutor = Executors.newCachedThreadPool()

    val subscriptions = mutableMapOf<String, MutableList<BananaSubscriptionData>>()

    init {
        jedisConfig.minIdle = 2
        jedisConfig.maxIdle = 8
        jedisConfig.minEvictableIdleTimeMillis = 1_000L * 60L * 5L

        subscribeJedisPool = JedisPool(
            jedisConfig,
            credentials.address,
            credentials.port
        )
        publishJedisPool = JedisPool(
            jedisConfig,
            credentials.address,
            credentials.port
        )
    }

    fun subscribe() {
        thread(name = "Banana - Subscribe Thread") {
            useResource({
                try {
                    it.subscribe(
                        BananaSubscription(this),
                        options.channel
                    )
                } finally {
                    it.connect()
                }
            }, subscribeJedisPool)
        }

        useResource {
            it.connect()
        }

        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(
                Thread {
                    if (!publishJedisPool.isClosed) {
                        publishJedisPool.close()
                    }

                    if (!subscribeJedisPool.isClosed) {
                        subscribeJedisPool.close()
                    }

                    if (pubSub.isSubscribed) {
                        pubSub.unsubscribe()
                    }

                    println("[Banana] Disconnected from jedis channel \"${options.channel}\".")
                }
            )

            shutdownHookAdded = true
        }
    }

    fun useResource(lambda: (Jedis) -> Unit) {
        useResource(lambda, publishJedisPool)
    }

    fun useResource(
        lambda: (Jedis) -> Unit,
        jedisPool: JedisPool?
    ) {
        try {
            val resource = jedisPool?.resource
                ?: throw RuntimeException("Jedis pool provided was null")

            if (credentials.authenticate) {
                resource.auth(credentials.password)
            }

            lambda.invoke(resource)

            if (!jedisPool.isClosed) {
                resource.close()
            }
        } catch (e: Exception) {
            options.exceptionHandler.onOutgoingPacket(
                "CMD", e
            )
        }
    }

    fun registerClass(clazz: Class<*>) {
        val instance = clazz.newInstance()
        validateClass(instance)

        clazz.methods.forEach {
            registerMethod(it, instance)
        }
    }

    private fun validateClass(instance: Any?) {
        instance ?: throw RuntimeException("Provided subscription instance for Banana was null")
    }

    fun registerClass(clazz: Any) {
        validateClass(clazz)

        clazz::class.java.methods.forEach {
            registerMethod(it, clazz)
        }
    }

    private fun registerMethod(method: Method, instance: Any) {
        val subscribe = method.getAnnotation(Subscribe::class.java) ?: return
        subscriptions.putIfAbsent(subscribe.value, mutableListOf())

        val packetSubscriptions = subscriptions[subscribe.value]

        if (options.debug) {
            println("Registered ${subscribe.value}")
        }

        packetSubscriptions!!.add(
            BananaSubscriptionData(
                instance, method, subscribe
            )
        )
    }
}
