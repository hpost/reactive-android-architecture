package cc.femto.architecture.reactive.di

import android.content.Context
import cc.femto.architecture.reactive.data.BuildType
import com.google.gson.Gson
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(buildType: BuildType): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = when (buildType) {
                BuildType.DEBUG -> HttpLoggingInterceptor.Level.BASIC
                else -> HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideChuckInterceptor(context: Context): ChuckInterceptor =
        ChuckInterceptor(context)

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideRxJavaCallAdapterFactory(scheduler: Scheduler): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.createWithScheduler(scheduler)

    @Provides
    fun provideRetrofitScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    fun providePicasso(context: Context): Picasso =
        Picasso.Builder(context)
            .listener { _, uri, e -> Timber.e(e, "Failed to load image: %s", uri) }
            .build()
}
