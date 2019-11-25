package cc.femto.architecture.reactive

import cc.femto.architecture.reactive.data.Session
import cc.femto.architecture.reactive.data.SessionEvent
import cc.femto.spek.extras.rxGroup
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.nhaarman.mockitokotlin2.*
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SessionSpec : Spek({

    lateinit var querySubject: PublishSubject<Optional<String>>
    lateinit var queryPreference: Preference<Optional<String>>
    lateinit var rxPreferences: RxSharedPreferences
    lateinit var session: Session

    val persistedQuery = "rx-mvi"

    beforeEachTest {
        querySubject = PublishSubject.create()
        queryPreference = mock {
            on { asObservable() } doReturn querySubject
        }
        rxPreferences = mock {
            on { getObject(eq("query"), any<Optional<String>>(), any()) } doReturn queryPreference
        }
        session = Session(rxPreferences, gson = mock())
    }

    rxGroup("Session") {

        describe("query") {
            lateinit var observer: TestObserver<SessionEvent.Query>
            beforeEachTest {
                observer = session.query().test()
            }

            describe("observable") {
                beforeEachTest {
                    querySubject.onNext(persistedQuery.toOptional())
                }
                it("emits correct events") {
                    observer.assertValue(
                        SessionEvent.Query(persistedQuery)
                    )
                }
            }

            describe("set") {
                beforeEachTest {
                    session.setQuery(persistedQuery)
                }
                it("persists correct value") {
                    verify(queryPreference).set(persistedQuery.toOptional())
                }
            }
        }
    }
})
