package cc.femto.architecture.reactive.components.home.di

import cc.femto.architecture.reactive.appComponent
import cc.femto.architecture.reactive.components.home.HomeActivity

fun HomeActivity.inject() {
    appComponent().inject(this)
}
