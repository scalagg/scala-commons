package net.evilblock.cubed.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.evilblock.cubed.serializers.impl.UUIDAdapter
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import java.util.*

object Serializers
{
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter)
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
            this.gson = gsonBuilder.create()
        }
    }

    fun useGsonBuilderThenRebuild(
        use: (GsonBuilder) -> Unit
    )
    {
        synchronized(gsonBuilder) {
            use.invoke(this.gsonBuilder)
            this.gson = this.gsonBuilder.create()
        }
    }
}
