package cc.femto.architecture.reactive.components.repositories

import cc.femto.architecture.reactive.data.model.Repository
import cc.femto.mvi.Action

data class RepositoriesState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: Throwable? = null,
    val repositories: List<Repository> = emptyList()
)

sealed class RepositoriesAction : Action {
    data class SearchRepositories(val query: String) : RepositoriesAction()
    data class TapOnRepository(val repository: Repository) : RepositoriesAction()
}
