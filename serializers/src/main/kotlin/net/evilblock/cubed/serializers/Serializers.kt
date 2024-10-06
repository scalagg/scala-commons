package net.evilblock.cubed.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import net.evilblock.cubed.serializers.impl.UUIDAdapter
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.text.DateFormat
import java.util.*

object Serializers
{
    private val gsonBuilder = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .registerTypeAdapter(UUID::class.java, UUIDAdapter)
        .setDateFormat(DateFormat.FULL, DateFormat.FULL)
        .serializeNulls()

    fun builder() = this.gsonBuilder

    @JvmStatic
    var gson: Gson = gsonBuilder.create()

    fun create(
        use: GsonBuilder.() -> Unit
    )
    {
        synchronized(gsonBuilder) {
            this.gsonBuilder.use()
            this.gson = gsonBuilder
                .registerTypeAdapter(UUID::class.java, UUIDAdapter)
                .create()
        }
    }

    fun useGsonBuilderThenRebuild(
        use: (GsonBuilder) -> Unit
    )
    {
        synchronized(gsonBuilder) {
            use.invoke(this.gsonBuilder)
            this.gson = this.gsonBuilder
                .registerTypeAdapter(UUID::class.java, UUIDAdapter)
                .create()
        }
    }
}
