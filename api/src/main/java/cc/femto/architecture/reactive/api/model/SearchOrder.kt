package cc.femto.architecture.reactive.api.model

enum class SearchOrder(val value: String) {
    ASC("asc"),
    DESC("desc");

    override fun toString() = value
}
