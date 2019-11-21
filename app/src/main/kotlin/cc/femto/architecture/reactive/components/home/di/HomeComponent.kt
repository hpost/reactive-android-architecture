package cc.femto.architecture.reactive.components.home.di

import cc.femto.architecture.reactive.components.home.HomeActivity
import cc.femto.architecture.reactive.components.home.HomeLayout
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesLayout
import cc.femto.architecture.reactive.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface HomeComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeComponent
    }

    fun inject(activity: HomeActivity)
    fun inject(layout: HomeLayout)
    fun inject(layout: RepositoriesLayout)
}
