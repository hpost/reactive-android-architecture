package cc.femto.architecture.reactive.di

import cc.femto.architecture.reactive.App
import cc.femto.architecture.reactive.components.home.HomeActivity
import cc.femto.architecture.reactive.components.home.HomeLayout
import cc.femto.architecture.reactive.components.repositories.RepositoriesLayout
import dagger.Component
import javax.inject.Singleton

/**
 * A component whose lifetime matches that of the application
 */
@Singleton
@Component(
    modules = [
        AndroidModule::class,
        NetworkModule::class,
        ApiModule::class
    ]
)
interface AppComponent {

    fun inject(app: App)

    fun inject(activity: HomeActivity)
    fun inject(layout: HomeLayout)

    fun inject(layout: RepositoriesLayout)
}
