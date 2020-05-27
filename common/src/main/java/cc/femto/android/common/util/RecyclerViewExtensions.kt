package cc.femto.android.common.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.scrollChangeEvents
import io.reactivex.Observable

fun RecyclerView.pageLoadEvents(itemThreshold: Int = 10): Observable<Boolean> =
    this.scrollChangeEvents()
        .map {
            val totalItemCount = this.layoutManager?.itemCount ?: 0
            val lastVisibleItem = (this.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
            lastVisibleItem + itemThreshold > totalItemCount
                    && totalItemCount > itemThreshold / 2
        }
        .distinctUntilChanged()
        .filter { shouldLoadPage -> shouldLoadPage }
