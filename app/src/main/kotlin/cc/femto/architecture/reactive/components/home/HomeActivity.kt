package cc.femto.architecture.reactive.components.home

import cc.femto.architecture.reactive.App
import cc.femto.architecture.reactive.R
import cc.femto.common.activity.ModelViewIntentActivity
import javax.inject.Inject

class HomeActivity : ModelViewIntentActivity<HomeAction, HomeState>() {

    @Inject lateinit var model: HomeModel

    init {
        App.component.inject(this)
    }

    override fun layout() = R.layout.home_layout

    override fun model() = model
}
