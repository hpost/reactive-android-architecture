package cc.femto.architecture.reactive.di

import android.content.Context
import cc.femto.architecture.reactive.data.BuildType
import com.google.gson.Gson
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckInterceptor: ChuckInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
            .addInterceptor(chuckInterceptor)
            .build()

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
    fun provideRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    fun providePicasso(context: Context): Picasso =
        Picasso.Builder(context)
            .listener { _, uri, e -> Timber.e(e, "Failed to load image: %s", uri) }
            .build()
}
