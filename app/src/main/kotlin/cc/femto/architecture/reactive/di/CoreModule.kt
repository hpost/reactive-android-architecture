package cc.femto.architecture.reactive.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
}
