package cc.femto.architecture.reactive.api.model

import com.google.gson.annotations.SerializedName

data class Owner(
    val id: Int = -1,
    val login: String = "",
    @SerializedName("avatar_url") val avatarUrl: String = ""
)
