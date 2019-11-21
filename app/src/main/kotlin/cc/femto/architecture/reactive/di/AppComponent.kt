package cc.femto.architecture.reactive.di

import android.content.Context
import cc.femto.architecture.reactive.App
import cc.femto.architecture.reactive.api.di.ApiModule
import cc.femto.architecture.reactive.components.home.di.HomeComponent
import cc.femto.architecture.reactive.data.BuildType
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Component that matches the [android.app.Application] lifetime
 */
@Singleton
@Component(
    modules = [
        AppSubcomponentsModule::class,
        CoreModule::class,
        StorageModule::class,
        NetworkModule::class,
        ApiModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance buildType: BuildType
        ): AppComponent
    }

    fun homeComponent(): HomeComponent.Factory

    fun inject(app: App)
}
