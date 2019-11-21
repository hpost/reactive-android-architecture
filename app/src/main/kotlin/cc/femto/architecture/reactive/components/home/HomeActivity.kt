package cc.femto.architecture.reactive.components.home

import android.os.Bundle
import cc.femto.architecture.reactive.R
import cc.femto.architecture.reactive.components.home.di.inject
import cc.femto.common.activity.ModelViewIntentActivity
import javax.inject.Inject

class HomeActivity : ModelViewIntentActivity<HomeAction, HomeState>() {

    @Inject lateinit var model: HomeModel

    override fun layout() = R.layout.home_layout

    override fun model() = model

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }
}
