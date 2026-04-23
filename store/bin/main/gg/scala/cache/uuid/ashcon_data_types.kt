package gg.scala.cache.uuid

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author GrowlyX
 * @since 9/13/2022
 */
data class AshconUsername(
    val username: String
)

data class AshconSignatureEntry(
    val value: String,
    val signature: String
)

data class AshconSkinData(
    val url: String,
    val data: String,
    val raw: AshconSignatureEntry
)

data class AshconSkin(
    val custom: Boolean,
    val slim: Boolean,
    val skin: AshconSkinData
)

data class AshconResponse(
    val uuid: UUID,
    val username: String,
    @SerializedName("username_history")
    val usernameHistory: List<AshconUsername>,
    val textures: AshconSkin,
    @SerializedName("created_at")
    val creationDate: String?,
    // error responses
    val code: Int?,
    val error: String?,
    val reason: String?,
)


