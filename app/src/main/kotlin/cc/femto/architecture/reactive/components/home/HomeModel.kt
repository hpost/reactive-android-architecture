package cc.femto.architecture.reactive.components.home

import cc.femto.android.common.mvi.LoggingModel
import cc.femto.architecture.reactive.data.Session
import cc.femto.architecture.reactive.data.SessionEvent
import cc.femto.architecture.reactive.di.ActivityScope
import cc.femto.mvi.Event
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

@ActivityScope
class HomeModel @Inject constructor(
    private val session: Session
) : LoggingModel<HomeAction, HomeState>() {

    override fun initialState() = HomeState()

    override fun makeStateMutations(actions: Observable<HomeAction>): Observable<out Event> {
        val persistedQuery = session.query()

        return Observable.mergeArray(
            persistedQuery
        )
    }

    override fun reduce(state: HomeState, event: Event) = when (event) {
        is SessionEvent.Query -> state.copy(
            query = event.query
        )
        else -> state
    }

    override fun makeSideEffects(actions: Observable<HomeAction>): CompositeDisposable {
        val queryInput = actions.ofType<HomeAction.QueryInput>()
            .distinctUntilChanged()
            .map { QueryInputEvent(it.query) }

        val clearQuery = actions.ofType<HomeAction.ClearQuery>()
            .map { QueryInputEvent("") }

        val persistQuery = Observable.merge(queryInput, clearQuery)
            .subscribe { (query) -> session.setQuery(query) }

        return CompositeDisposable(
            persistQuery
        )
    }

    private data class QueryInputEvent(val query: String) : Event
}
