package cc.femto.architecture.reactive.api.model

import com.google.gson.annotations.SerializedName

enum class SearchOrder {
    @SerializedName("asc")
    ASC,
    @SerializedName("desc")
    DESC
}
