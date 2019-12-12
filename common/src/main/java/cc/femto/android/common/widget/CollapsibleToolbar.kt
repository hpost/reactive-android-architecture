package cc.femto.android.common.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * [MotionLayout] that expects to be nested inside an [AppBarLayout]
 *
 * Registers a [AppBarLayout.OnOffsetChangedListener]
 * and drives the progress of the [MotionLayout].
 */
class CollapsibleToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr),
    AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset / (appBarLayout?.totalScrollRange?.toFloat() ?: 1f)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }
}
