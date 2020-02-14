package cc.femto.android.common.activity

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.app.ComponentActivity
import cc.femto.kommon.extensions.hideKeyboard
import com.gojuno.koptional.None
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.math.absoluteValue

/**
 * Activity that wraps and delegates functionality to a hosted custom view
 */
abstract class ViewContainerActivity : ComponentActivity() {

    private val intentSubject: BehaviorSubject<Intent> = BehaviorSubject.create()
    private val backPressedSubject: PublishSubject<None> = PublishSubject.create()
    private val isResumedSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val activityResultSubjects: MutableMap<Int, PublishSubject<Instrumentation.ActivityResult>> = mutableMapOf()

    protected var contentView: ViewGroup? = null
        private set

    /**
     * Called in ViewContainerActivity#onCreate
     *
     * @return The resource ID of the view that should be embedded in this Activity
     */
    abstract val viewId: Int

    /**
     * Emits the latest [Intent] that was captured during [onCreate] or [onNewIntent]
     */
    fun intentObservable(): Observable<Intent> = intentSubject

    /**
     * Emits events when the back button is pressed
     */
    fun backPresses(): Observable<None> = backPressedSubject

    /**
     * Emits events when the [Activity] pauses and resumes
     */
    fun isResumed(): Observable<Boolean> = isResumedSubject

    /**
     * Wrapper for [startActivityForResult] which returns an [Observable] that will emit the result
     */
    fun startActivityForResultObservable(
        intent: Intent,
        requestCode: Int = UUID.randomUUID().hashCode().absoluteValue,
        options: Bundle? = null
    ): Observable<Instrumentation.ActivityResult> {
        val subject: PublishSubject<Instrumentation.ActivityResult> = PublishSubject.create()
        activityResultSubjects[requestCode] = subject
        startActivityForResult(intent, requestCode, options)
        return subject.take(1)
    }

    /**
     * Inflates the layout identified by [viewId] and sets it as the Activity's [contentView]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentView = layoutInflater.inflate(viewId, null, false) as ViewGroup
        setContentView(contentView)

        intentSubject.onNext(intent)
    }

    override fun onPause() {
        super.onPause()
        isResumedSubject.onNext(false)
        try {
            hideKeyboard()
        } catch (t: Throwable) {
            /* no-op */
        }
    }

    override fun onResume() {
        super.onResume()
        isResumedSubject.onNext(true)
    }

    override fun onNewIntent(intent: Intent) {
        intentSubject.onNext(intent)
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        backPressedSubject.onNext(None)
        var consumed = false
        if (contentView is OnBackPressedListener) {
            consumed = (contentView as OnBackPressedListener).onBackPressed()
        }
        if (!consumed && !backPressedSubject.hasObservers()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultSubjects[requestCode]?.onNext(Instrumentation.ActivityResult(resultCode, data))
        activityResultSubjects.remove(requestCode)
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }
}
