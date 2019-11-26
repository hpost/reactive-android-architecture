package cc.femto.android.common.mvi

import cc.femto.kommon.extensions.d
import cc.femto.mvi.Action
import cc.femto.mvi.BaseModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

abstract class LoggingModel<ACTION : Action, STATE> : BaseModel<ACTION, STATE>() {

    override fun attach(actions: Observable<ACTION>) {
        super.attach(actions)
        disposables += actions.subscribe { d("action: $it") }
        disposables += events().subscribe { d("event: $it") }
    }
}
