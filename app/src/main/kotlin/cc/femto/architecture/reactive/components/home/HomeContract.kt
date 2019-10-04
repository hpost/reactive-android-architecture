package cc.femto.architecture.reactive.components.home

import cc.femto.mvi.Action

data class HomeState(
    val query: String = ""
)

sealed class HomeAction : Action {
    data class QueryInput(val query: String) : HomeAction()
    object ClearQuery : HomeAction()
}
