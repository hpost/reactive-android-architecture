package cc.femto.architecture.reactive.api.model

import com.google.gson.annotations.SerializedName

data class Repository(
    val id: Int = -1,
    val name: String = "",
    @SerializedName("full_name") val fullName: String = "",
    val owner: Owner = Owner(),
    val description: String = "",
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("stargazers_count") val stargazersCount: Int = 0
)