package cc.femto.architecture.reactive.data.model

data class SearchRepositoriesResponse(
    val total_count: Int,
    val items: List<Repository>
)
