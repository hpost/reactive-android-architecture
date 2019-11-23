package cc.femto.architecture.reactive.components.home

import cc.femto.android.common.activity.ModelViewIntentActivity
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.appComponent

class HomeActivity : ModelViewIntentActivity<HomeAction, HomeState>() {

    val homeComponent by lazy { appComponent().homeComponent().create(this) }

    override fun model() = homeComponent.homeModel()

    override fun layout() = R.layout.home_layout
}
