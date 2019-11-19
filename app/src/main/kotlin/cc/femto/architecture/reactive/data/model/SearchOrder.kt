package cc.femto.architecture.reactive.data.model

import com.google.gson.annotations.SerializedName

enum class SearchOrder {
    @SerializedName("asc")
    ASC,
    @SerializedName("desc")
    DESC
}
