package cc.femto.architecture.reactive.components.home.di

import android.app.Activity
import cc.femto.architecture.reactive.components.home.HomeModel
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesModel
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

    fun homeModel(): HomeModel
    fun repositoriesModel(): RepositoriesModel
}
