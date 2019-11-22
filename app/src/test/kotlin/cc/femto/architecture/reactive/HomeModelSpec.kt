package cc.femto.architecture.reactive

import cc.femto.architecture.reactive.components.home.HomeAction
import cc.femto.architecture.reactive.components.home.HomeModel
import cc.femto.architecture.reactive.components.home.HomeState
import cc.femto.architecture.reactive.data.Session
import cc.femto.architecture.reactive.data.SessionEvent
import cc.femto.spek.extras.rxGroup
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class HomeModelSpec : Spek({

    lateinit var querySubject: PublishSubject<SessionEvent.Query>
    lateinit var session: Session
    lateinit var actions: Subject<HomeAction>
    lateinit var model: HomeModel
    lateinit var observer: TestObserver<HomeState>

    val persistedQuery = "rx-mvi"
    val initialState = HomeState(
        query = persistedQuery
    )

    beforeEachTest {
        querySubject = PublishSubject.create()
        session = mock {
            on { query() } doReturn querySubject.startWith(
                SessionEvent.Query(persistedQuery)
            )
        }
        actions = PublishSubject.create()
        model = HomeModel(session)
    }

    rxGroup("HomeModel") {
        beforeEachTest {
            model.attach(actions)
            observer = model.state().test()
        }

        describe("initial state") {
            it("observes persisted query from session") {
                verify(session).query()
            }
            it("emits correct state") {
                observer.assertValue(initialState)
            }
        }

        describe("query updates") {
            val update = "query update"
            beforeEachTest {
                querySubject.onNext(SessionEvent.Query(update))
            }
            it("updates state") {
                observer.assertValues(
                    initialState,
                    initialState.copy(query = update)
                )
            }
        }

        describe("query input") {
            val input = "query input"
            beforeEachTest {
                actions.onNext(HomeAction.QueryInput(input))
            }
            it("persists query in session") {
                verify(session).setQuery(input)
            }
        }

        describe("clear query") {
            beforeEachTest {
                actions.onNext(HomeAction.ClearQuery)
            }
            it("clears query in session") {
                verify(session).setQuery("")
            }
        }
    }
})
