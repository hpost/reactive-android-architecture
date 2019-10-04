package cc.femto.architecture.reactive.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import cc.femto.architecture.reactive.data.Session
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module for Android-specific dependencies which require a [android.content.Context]
 * or [android.app.Application] to create.
 */
@Module
class AndroidModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    @Singleton
    fun provideSession(sharedPreferences: SharedPreferences, gson: Gson): Session =
        Session(sharedPreferences, gson)
}

