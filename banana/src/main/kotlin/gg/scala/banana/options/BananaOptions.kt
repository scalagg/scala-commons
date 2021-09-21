package gg.scala.banana.options

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.banana.handler.ExceptionHandler
import gg.scala.banana.handler.impl.DefaultExceptionHandler
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * Holds general information for the
 * [gg.scala.banana.Banana] instance to use.
 *
 * @author GrowlyX
 * @since 9/19/2021
 */
data class BananaOptions(
    val channel: String,

    val async: Boolean = true,
    val gson: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create(),
    val exceptionHandler: ExceptionHandler = DefaultExceptionHandler,
    val executor: Executor = ForkJoinPool.commonPool()
)
