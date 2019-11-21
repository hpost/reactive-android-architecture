package cc.femto.architecture.reactive.api.model

import com.google.gson.annotations.SerializedName

enum class SearchSort {
    @SerializedName("stars")
    STARS,
    @SerializedName("forks")
    FORKS,
    @SerializedName("help-wanted-issues")
    HELP_WANTED_ISSUES,
    @SerializedName("updated")
    UPDATED
}
