package cc.femto.architecture.reactive.components.home

import cc.femto.architecture.reactive.di.ActivityScope
import cc.femto.kommon.extensions.v
import cc.femto.mvi.BaseModel
import cc.femto.mvi.Event
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

@ActivityScope
class HomeModel @Inject constructor() : BaseModel<HomeAction, HomeState>() {

    override fun attach(actions: Observable<HomeAction>) {
        super.attach(actions)
        disposables += actions.subscribe { v("action: $it") }
        disposables += events().subscribe { v("event: $it") }
    }

    override fun initialState() = HomeState()

    override fun makeStateMutations(actions: Observable<HomeAction>): Observable<out Event> {
        val queryInput = actions.ofType<HomeAction.QueryInput>()
            .distinctUntilChanged()
            .map { QueryInputEvent(it.query) }

        val clearQuery = actions.ofType<HomeAction.ClearQuery>()
            .map { QueryInputEvent("") }

        return Observable.mergeArray(
            queryInput,
            clearQuery
        )
    }

    override fun reduce(state: HomeState, event: Event) = when (event) {
//        is InitEvent -> state.copy(
//            init = event.init
//        )
        is QueryInputEvent -> state.copy(
            query = event.query
        )
        else -> state
    }

//    override fun makeSideEffects(actions: Observable<HomeAction>): CompositeDisposable {
//        val foo = events().ofType<TestEvent>()
//            .subscribe {
//                /* side-effect */
//            }
//
//        return CompositeDisposable(
//            foo
//        )
//    }

    data class InitEvent(val init: Int) : Event
    private data class QueryInputEvent(val query: String) : Event
}
