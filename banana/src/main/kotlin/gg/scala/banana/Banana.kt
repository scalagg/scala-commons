package gg.scala.banana

import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.helper.BananaHelper
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.BananaSubscription
import gg.scala.banana.subscribe.BananaSubscriptionData
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture

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
    companion object {
        @JvmStatic
        val bananas = mutableListOf<Banana>()
    }

    private val jedisConfig = JedisPoolConfig()

    private val subscribeJedisPool: JedisPool
    private var publishJedisPool: JedisPool

    private lateinit var pubSub: JedisPubSub

    private var shutdownHookAdded = false

    val subscriptions = mutableMapOf<String, MutableList<BananaSubscriptionData>>()

    init {
        bananas.add(this)

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


        CompletableFuture.runAsync {
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

        BananaHelper.handle({
            useResource {
                it.connect()
            }
        }, this)

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

            if (resource != null) {
                if (credentials.authenticate) {
                    resource.auth(credentials.password)
                }

                lambda.invoke(resource)
            } else {
                throw RuntimeException("Jedis pool provided was null")
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
