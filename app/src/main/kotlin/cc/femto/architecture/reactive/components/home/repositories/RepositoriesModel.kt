package cc.femto.architecture.reactive.components.home.repositories

import cc.femto.android.common.mvi.LoggingModel
import cc.femto.architecture.reactive.api.SearchApi
import cc.femto.architecture.reactive.api.SearchRepositoriesEvent
import cc.femto.architecture.reactive.api.model.SearchSort
import cc.femto.architecture.reactive.di.ActivityScope
import cc.femto.architecture.reactive.navigation.Navigator
import cc.femto.mvi.Event
import cc.femto.rx.extensions.mapOnce
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

@ActivityScope
class RepositoriesModel @Inject constructor(
    private val searchApi: SearchApi,
    private val navigator: Navigator
) : LoggingModel<RepositoriesAction, RepositoriesState>() {

    override fun initialState() = RepositoriesState()

    override fun makeStateMutations(actions: Observable<RepositoriesAction>): Observable<out Event> {
        val searchRepositories = Observable.merge(
            events().ofType<InitEvent>().map { it.query },
            actions.ofType<RepositoriesAction.RetrySearch>()
                .flatMap { state().mapOnce { query } }
        )
            .filter { query -> query.isNotBlank() }
            .switchMap { query -> searchApi.searchRepositories(query, sort = SearchSort.STARS) }

        return Observable.mergeArray(
            searchRepositories
        )
    }

    override fun reduce(state: RepositoriesState, event: Event) = when (event) {
        is InitEvent -> state.copy(
            query = event.query
        )
        is SearchRepositoriesEvent.InProgress -> state.copy(
            isLoading = true,
            isError = false,
            error = null
        )
        is SearchRepositoriesEvent.Success -> state.copy(
            isLoading = false,
            isError = false,
            repositories = event.repositories
        )
        is SearchRepositoriesEvent.Error -> state.copy(
            isLoading = false,
            isError = true,
            error = event.error
        )
        else -> state
    }

    override fun makeSideEffects(actions: Observable<RepositoriesAction>): CompositeDisposable {
        val openRepositoryDetails = actions.ofType<RepositoriesAction.TapOnRepository>()
            .subscribe { navigator.navigateToUrl(it.repository.htmlUrl) }

        return CompositeDisposable(
            openRepositoryDetails
        )
    }

    data class InitEvent(val query: String) : Event
}
