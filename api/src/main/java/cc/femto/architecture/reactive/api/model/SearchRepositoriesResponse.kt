package cc.femto.architecture.reactive.api.model

data class SearchRepositoriesResponse(
    val total_count: Int,
    val items: List<Repository>
)
