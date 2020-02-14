package cc.femto.architecture.reactive.components.home.di

import cc.femto.android.common.activity.ViewContainerActivity
import cc.femto.architecture.reactive.components.home.HomeModel
import cc.femto.architecture.reactive.components.home.repositories.RepositoriesModel
import cc.femto.architecture.reactive.di.ActivityScope
import cc.femto.architecture.reactive.di.AppComponent
import dagger.BindsInstance
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface HomeComponent {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance activity: ViewContainerActivity
        ): HomeComponent
    }

    fun homeModel(): HomeModel
    fun repositoriesModel(): RepositoriesModel
}
