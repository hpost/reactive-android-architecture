package cc.femto.architecture.reactive.navigation

import android.app.Activity
import android.content.Intent
import cc.femto.architecture.reactive.di.ActivityScope
import org.jetbrains.anko.browse
import java.io.Serializable
import javax.inject.Inject

@ActivityScope
class Navigator @Inject constructor(private val activity: Activity) {

    companion object {
        const val RESULT_DATA = "result_data"
    }

    fun finishActivity(success: Boolean = false, data: Serializable? = null) {
        activity.setResult(
            when {
                success -> Activity.RESULT_OK
                else -> Activity.RESULT_CANCELED
            },
            Intent().apply {
                putExtra(RESULT_DATA, data)
            })
        activity.finishAfterTransition()
    }

    fun navigateToUrl(url: String) {
        activity.browse(url)
    }
}
