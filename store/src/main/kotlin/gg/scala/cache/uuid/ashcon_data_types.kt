package gg.scala.cache.uuid

import com.google.gson.annotations.SerializedName
import java.util.*

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

    val code: Int?,
    val error: String?,
    val reason: String?,
)

