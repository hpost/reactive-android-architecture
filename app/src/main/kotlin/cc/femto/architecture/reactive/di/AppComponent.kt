package cc.femto.architecture.reactive.di

import android.content.Context
import android.content.SharedPreferences
import cc.femto.architecture.reactive.App
import cc.femto.architecture.reactive.api.SearchApi
import cc.femto.architecture.reactive.api.di.ApiModule
import cc.femto.architecture.reactive.data.BuildType
import cc.femto.architecture.reactive.data.Session
import com.squareup.picasso.Picasso
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Component that matches the scope of [android.app.Application]
 */
@Singleton
@Component(
    modules = [
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

    fun picasso(): Picasso
    fun sharedPreferences(): SharedPreferences
    fun session(): Session
    fun searchApi(): SearchApi

    fun inject(app: App)
}
