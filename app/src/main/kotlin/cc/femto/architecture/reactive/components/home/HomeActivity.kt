package cc.femto.architecture.reactive.components.home

import cc.femto.android.common.activity.ModelViewIntentActivity
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.components.home.di.DaggerHomeComponent

class HomeActivity : ModelViewIntentActivity<HomeAction, HomeState>() {

    val homeComponent by lazy { DaggerHomeComponent.factory().create(appComponent(), this) }

    override fun model() = homeComponent.homeModel()

    override fun layout() = R.layout.home_layout
}
