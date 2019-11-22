package cc.femto.architecture.reactive.components.home.di

import android.app.Activity
import cc.femto.architecture.reactive.components.home.HomeActivity
import cc.femto.architecture.reactive.components.home.HomeLayout
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesLayout
import cc.femto.architecture.reactive.di.ActivityScope
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface HomeComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: Activity): HomeComponent
    }

    fun inject(activity: HomeActivity)
    fun inject(layout: HomeLayout)
    fun inject(layout: RepositoriesLayout)
}
