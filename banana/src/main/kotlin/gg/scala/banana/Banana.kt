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
import java.util.function.Consumer
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
)
{
    private val subscribeJedisPool: JedisPool
    var publishJedisPool: JedisPool

    private lateinit var pubSub: JedisPubSub

    private var shutdownHookAdded = false

    val subscriptions = mutableMapOf<String, MutableList<BananaSubscriptionData>>()

    init
    {
        subscribeJedisPool = JedisPool(
            credentials.address,
            credentials.port
        )
        publishJedisPool = JedisPool(
            credentials.address,
            credentials.port
        )
    }

    fun subscribe()
    {
        pubSub = BananaSubscription(this)

        thread(name = "Banana - Subscribe Thread") {
            useResource({
                it.subscribe(
                    pubSub, options.channel
                )
            }, subscribeJedisPool)
        }

        useResource {
            it.connect()
        }

        if (!shutdownHookAdded)
        {
            Runtime.getRuntime().addShutdownHook(
                Thread {
                    if (!publishJedisPool.isClosed)
                    {
                        publishJedisPool.close()
                    }

                    if (!subscribeJedisPool.isClosed)
                    {
                        subscribeJedisPool.close()
                    }

                    try
                    {
                        if (pubSub.isSubscribed)
                        {
                            pubSub.unsubscribe()
                        }
                    } catch (ignored: Exception)
                    {
                        println("[Banana] We were never subscribed to: ${options.channel}.")
                    }

                    println("[Banana] Disconnected from jedis channel \"${options.channel}\".")
                }
            )

            shutdownHookAdded = true
        }
    }

    fun useResource(lambda: Consumer<Jedis>)
    {
        useResource({
            lambda.accept(it)
        }, publishJedisPool)
    }

    fun <T> useResource(lambda: (Jedis) -> T): T?
    {
        return useResource(lambda, publishJedisPool)
    }

    private fun <T> useResource(
        lambda: (Jedis) -> T,
        jedisPool: JedisPool?
    ): T?
    {
        val resource = jedisPool?.resource
            ?: throw RuntimeException("Jedis pool provided was null")

        try
        {
            if (credentials.authenticate)
            {
                resource.auth(credentials.password)
            }

            val thing = lambda
                .invoke(resource)

            if (!jedisPool.isClosed)
            {
                resource.close()
            }

            return thing
        } catch (e: Exception)
        {
            options.exceptionHandler.onOutgoingPacket(
                "CMD", e
            )

            publishJedisPool.returnResource(resource)
        }

        return null
    }

    fun registerClass(clazz: Class<*>)
    {
        val instance = clazz.newInstance()
        validateClass(instance)

        clazz.methods.forEach {
            registerMethod(it, instance)
        }
    }

    private fun validateClass(instance: Any?)
    {
        instance ?: throw RuntimeException("Provided subscription instance for Banana was null")
    }

    fun registerClass(clazz: Any)
    {
        validateClass(clazz)

        clazz::class.java.methods.forEach {
            registerMethod(it, clazz)
        }
    }

    private fun registerMethod(method: Method, instance: Any)
    {
        val subscribe = method.getAnnotation(Subscribe::class.java) ?: return
        subscriptions.putIfAbsent(subscribe.value, mutableListOf())

        val packetSubscriptions = subscriptions[subscribe.value]

        if (options.debug)
        {
            println("Registered ${subscribe.value}")
        }

        packetSubscriptions!!.add(
            BananaSubscriptionData(
                instance, method, subscribe
            )
        )
    }
}
