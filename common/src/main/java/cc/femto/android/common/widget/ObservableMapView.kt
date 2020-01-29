package cc.femto.android.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

open class ObservableMapLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val consumeClicks: Boolean = false
) : MapView(context, attrs, defStyleAttr), OnMapReadyCallback {

    protected var map: GoogleMap? = null
    private val mapSubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create()
    private val markerClicksSubject: BehaviorSubject<Marker> = BehaviorSubject.create()
    private val infoWindowClicksSubject: BehaviorSubject<Marker> = BehaviorSubject.create()

    /**
     * Emits the [GoogleMap] instance once its ready to be used
     */
    fun mapObservable(): Observable<GoogleMap> = mapSubject.take(1)

    /**
     * Emits the [Marker] that was clicked
     */
    fun markerClicks(): Observable<Marker> = markerClicksSubject

    /**
     * Emits the [Marker] for the InfoWindow that was clicked
     */
    fun infoWindowClicks(): Observable<Marker> = infoWindowClicksSubject

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        onCreate(null)
        getMapAsync(this)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (isInEditMode) {
            return
        }
        if (visibility == View.VISIBLE) {
            onResume()
        } else {
            onPause()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            onDestroy()
        } catch (t: Throwable) {
            /* ignore */
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.setOnMarkerClickListener {
            markerClicksSubject.onNext(it)
            consumeClicks // resume with default behavior if false
        }
        map?.setOnInfoWindowClickListener {
            infoWindowClicksSubject.onNext(it)
        }
        map?.setOnMapLoadedCallback {
            mapSubject.onNext(map)
        }
    }

    fun setVerticalPadding(top: Int, bottom: Int) {
        mapObservable().subscribe {
            it.setPadding(0, top, 0, bottom)
        }
    }
}
