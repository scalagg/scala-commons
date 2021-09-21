package gg.scala.banana

import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.BananaSubscription
import gg.scala.banana.subscribe.BananaSubscriptionData
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.lang.reflect.Method

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
    private var jedisPool: JedisPool? = null
    private val pubSub = BananaSubscription(this)

    private var shutdownHookAdded = false

    val subscriptions = mutableMapOf<String, MutableList<BananaSubscriptionData>>()

    fun subscribe() {
        jedisPool = JedisPool(
            credentials.address,
            credentials.port
        )

        Thread(
            {
                useResource {
                    it.subscribe(pubSub, options.channel)

                    println("[Banana] Now reading on jedis channel \"${options.channel}\".")
                }
            },
            "Banana Subscription Thread"
        ).start()

        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(
                Thread {
                    if (jedisPool != null && !jedisPool!!.isClosed) {
                        jedisPool?.close()
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
        jedisPool?.resource?.use { jedis ->
            credentials.password?.let {
                jedis.auth(it)
            }

            lambda.invoke(jedis)
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

        packetSubscriptions!!.add(
            BananaSubscriptionData(
                instance, method, subscribe
            )
        )
    }
}
