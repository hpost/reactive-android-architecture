package cc.femto.architecture.reactive.components.repositories

import cc.femto.architecture.reactive.data.api.SearchApi
import cc.femto.architecture.reactive.data.api.SearchRepositoriesEvent
import cc.femto.kommon.extensions.v
import cc.femto.mvi.BaseModel
import cc.femto.mvi.Event
import cc.femto.rx.extensions.mapOnce
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class RepositoriesModel @Inject constructor(
    private val searchApi: SearchApi
) : BaseModel<RepositoriesAction, RepositoriesState>() {

    override fun attach(actions: Observable<RepositoriesAction>) {
        super.attach(actions)
        disposables += actions.subscribe { v("action: $it") }
        disposables += events().subscribe { v("event: $it") }
    }

    override fun initialState() = RepositoriesState()

    override fun makeStateMutations(actions: Observable<RepositoriesAction>): Observable<out Event> {
        val searchRepositories = Observable.merge(
            events().ofType<InitEvent>().map { it.query },
            actions.ofType<RepositoriesAction.RetrySearch>()
                .flatMap { state().mapOnce { query } }
        )
            .filter { query -> query.isNotBlank() }
            .switchMap { query -> searchApi.searchRepositories(query, sort = "stars") }

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
            isError = false
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
        val openRepositoryDetails = events().ofType<RepositoriesAction.TapOnRepository>()
            .subscribe {
                TODO("open repository details")
            }

        return CompositeDisposable(
            openRepositoryDetails
        )
    }

    data class InitEvent(val query: String) : Event
}
