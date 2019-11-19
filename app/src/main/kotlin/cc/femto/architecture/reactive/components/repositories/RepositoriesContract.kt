package cc.femto.architecture.reactive.components.repositories

import cc.femto.architecture.reactive.data.model.Repository
import cc.femto.mvi.Action

data class RepositoriesState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: Throwable? = null,
    val query: String = "",
    val repositories: List<Repository> = emptyList()
)

sealed class RepositoriesAction : Action {
    data class TapOnRepository(val repository: Repository) : RepositoriesAction()
    object RetrySearch : RepositoriesAction()
}
