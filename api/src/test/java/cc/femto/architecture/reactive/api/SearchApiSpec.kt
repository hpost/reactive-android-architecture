package cc.femto.architecture.reactive.api

import cc.femto.spek.extras.rxGroup
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class SearchApiSpec : Spek({

    rxGroup("SearchApi") {
        describe("adding two numbers") {
            it("should yield the sum") {
                assertEquals(7, 3 + 4)
            }
        }
    }
})
