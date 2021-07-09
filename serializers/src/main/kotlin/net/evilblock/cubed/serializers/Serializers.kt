package net.evilblock.cubed.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.evilblock.cubed.serializers.impl.UUIDAdapter
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.util.*

object Serializers {

    private val gsonBuilder: GsonBuilder = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .serializeNulls()

    @JvmStatic
    var gson: Gson = gsonBuilder.create()

    val writerSettings: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

    fun useGsonBuilderThenRebuild(use: (GsonBuilder) -> Unit) {
        synchronized(gsonBuilder) {
            use.invoke(gsonBuilder)
            gson = gsonBuilder.create()
        }
    }
}