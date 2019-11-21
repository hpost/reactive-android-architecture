package cc.femto.architecture.reactive

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import cc.femto.architecture.reactive.data.BuildType
import cc.femto.architecture.reactive.di.AppComponent
import cc.femto.architecture.reactive.di.DaggerAppComponent
import cc.femto.kommon.Kommon
import cc.femto.kommon.extensions.e
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent() = DaggerAppComponent.factory()
        .create(
            context = applicationContext,
            buildType = when {
                BuildConfig.DEBUG -> BuildType.DEBUG
                else -> BuildType.RELEASE
            }
        )

    companion object {
        fun appComponent(context: Context) = (context.applicationContext as App).appComponent
    }

    init {
        RxJavaPlugins.setErrorHandler { throwable ->
            e("Unhandled error in stream: ${throwable.cause}", throwable)
            if (BuildConfig.DEBUG) {
                throw RuntimeException("Unhandled error in stream", throwable)
            }
        }
    }

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        appComponent.inject(this)
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

fun Activity.appComponent() = App.appComponent(this)
fun View.appComponent() = App.appComponent(context)
