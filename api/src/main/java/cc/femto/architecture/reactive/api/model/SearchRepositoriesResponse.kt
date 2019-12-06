package cc.femto.architecture.reactive.api.model

import com.google.gson.annotations.SerializedName

data class SearchRepositoriesResponse(
    @SerializedName("total_count") val totalCount: Int,
    val items: List<Repository>
)
