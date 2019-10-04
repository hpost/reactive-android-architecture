package cc.femto.architecture.reactive

import cc.femto.architecture.reactive.data.Session
import cc.femto.spek.extras.rxGroup
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class TestModelSpec : Spek({

    lateinit var session: Session

    rxGroup("TestModel") {
        beforeEachTest {
            session = mock()
        }

        describe("adding two numbers") {
            beforeEachTest {
                session.setQuery("foo")
            }
            it("should yield the sum") {
                assertEquals(7, 3 + 4)
            }
            it("should set the query") {
                verify(session).setQuery("foo")
            }
        }
    }
})
