package cc.femto.architecture.reactive.data.model

data class Repository(
    val id: Int,
    val name: String,
    val full_name: String,
    val owner: Owner,
    val description: String,
    val html_url: String,
    val stargazers_count: Int
)