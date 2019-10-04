package cc.femto.architecture.reactive.components

import cc.femto.mvi.BaseModel
import cc.femto.mvi.Event
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.ofType
import javax.inject.Inject

class TestModel @Inject constructor() : BaseModel<TestAction, TestState>() {

    override fun initialState() = TestState()

    override fun makeStateMutations(actions: Observable<TestAction>): Observable<out Event> {
        val foo = actions.ofType<TestAction.Foo>()
            .distinctUntilChanged()
            .map { TestEvent(it.value) }

        return Observable.mergeArray(foo)
    }

    override fun reduce(state: TestState, event: Event) = when (event) {
        is InitEvent -> state.copy(
            init = event.init
        )
        is TestEvent -> state.copy(
            foo = event.foo
        )
        else -> state
    }

    override fun makeSideEffects(actions: Observable<TestAction>): CompositeDisposable {
        val foo = events().ofType<TestEvent>()
            .subscribe {
                /* side-effect */
            }

        return CompositeDisposable(
            foo
        )
    }

    data class InitEvent(val init: Int) : Event
    private data class TestEvent(val foo: String) : Event
}
