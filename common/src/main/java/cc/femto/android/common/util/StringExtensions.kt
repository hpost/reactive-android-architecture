package cc.femto.android.common.util

import android.os.Build
import android.text.Html

@Suppress("DEPRECATION")
fun String.toHtmlSpan(): CharSequence {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).trim()
    } else {
        Html.fromHtml(this).trim()
    }
}
