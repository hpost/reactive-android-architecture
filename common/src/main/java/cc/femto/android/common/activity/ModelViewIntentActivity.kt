package cc.femto.android.common.activity

import android.os.Bundle
import cc.femto.mvi.Action
import cc.femto.mvi.Model
import cc.femto.mvi.View
import cc.femto.mvi.attachComponent

abstract class ModelViewIntentActivity<ACTION : Action, STATE> : ViewContainerActivity() {

    @Suppress("UNCHECKED_CAST")
    protected fun view(): View<ACTION, STATE> =
        contentView as? View<ACTION, STATE>
            ?: throw AssertionError("contentView must implement View<ACTION, STATE> interface")

    abstract fun model(): Model<ACTION, STATE>

    abstract fun layout(): Int

    override val viewId: Int
        get() = layout()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachComponent(view(), model())
    }

    override fun onDestroy() {
        model().detach()
        super.onDestroy()
    }
}