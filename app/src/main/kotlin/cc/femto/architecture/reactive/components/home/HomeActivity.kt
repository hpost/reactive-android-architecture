package cc.femto.architecture.reactive.components.home

import android.os.Bundle
import cc.femto.android.common.activity.ModelViewIntentActivity
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.components.home.di.HomeComponent
import javax.inject.Inject

class HomeActivity : ModelViewIntentActivity<HomeAction, HomeState>() {

    lateinit var homeComponent: HomeComponent

    @Inject
    lateinit var model: HomeModel

    override fun layout() = R.layout.home_layout

    override fun model() = model

    override fun onCreate(savedInstanceState: Bundle?) {
        homeComponent = appComponent().homeComponent().create(this)
        homeComponent.inject(this)
        super.onCreate(savedInstanceState)
    }
}
