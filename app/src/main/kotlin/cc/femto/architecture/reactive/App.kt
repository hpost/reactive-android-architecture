package cc.femto.architecture.reactive

import android.app.Application
import cc.femto.architecture.reactive.di.AndroidModule
import cc.femto.architecture.reactive.di.AppComponent
import cc.femto.architecture.reactive.di.DaggerAppComponent
import cc.femto.kommon.Kommon
import cc.femto.kommon.extensions.e
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var instance: Application
        val component: AppComponent by lazy {
            DaggerAppComponent.builder()
                .androidModule(AndroidModule(instance))
                .build()
        }
    }

    init {
        instance = this
        component.inject(this)

        RxJavaPlugins.setErrorHandler { throwable ->
            e("Unhandled error in stream: ${throwable.cause}", throwable)
            if (BuildConfig.DEBUG) {
                throw RuntimeException("Unhandled error in stream", throwable)
            }
        }
    }

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()

        initLogging()
        initLibraries()
    }

    override fun onTerminate() {
        disposables.clear()
        super.onTerminate()
    }

    private fun initLogging() {
        // Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            TODO("Implement tree that forwards logs to bug tracking solution")
        }
    }

    private fun initLibraries() {
        Kommon.init(this)
        AndroidThreeTen.init(this)
    }
}