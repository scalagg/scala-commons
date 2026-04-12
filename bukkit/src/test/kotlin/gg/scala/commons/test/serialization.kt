package gg.scala.commons.test

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.junit.jupiter.api.Test

class SerializationTests
{
    data class `Original model`(
        val defaultValueOne: String = "Hey"
    )

    data class `nullable model`(
        val defaultValueOne: String = "Hey",
        val defaultValueTwoNullable: String? = "Example"
    )

    @Test
    fun `test Gson, serializeNulls disabled`()
    {
        val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

        val original = `Original model`()

        println(gson.toJson(original))
        println(gson.fromJson(
            gson.toJson(original), `nullable model`::class.java
        ))
    }
}
