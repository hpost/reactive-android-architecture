package cc.femto.architecture.reactive.api.model

data class Repository(
    val id: Int = -1,
    val name: String = "",
    val full_name: String = "",
    val owner: Owner = Owner(),
    val description: String = "",
    val html_url: String = "",
    val stargazers_count: Int = 0
)