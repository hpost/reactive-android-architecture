package cc.femto.architecture.reactive.components

import cc.femto.mvi.Action

data class TestState(
    val init: Int? = null,
    val foo: String? = null
)

sealed class TestAction : Action {
    data class Foo(val value: String) : TestAction()
}

