package cc.femto.architecture.reactive.api.model

enum class SearchSort(val value: String) {
    STARS("stars"),
    FORKS("forks"),
    HELP_WANTED_ISSUES("help-wanted-issues"),
    UPDATED("updated");

    override fun toString() = value
}
