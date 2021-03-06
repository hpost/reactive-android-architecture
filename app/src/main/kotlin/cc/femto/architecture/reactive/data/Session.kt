package cc.femto.architecture.reactive.data

import cc.femto.android.common.util.gson.genericType
import cc.femto.android.common.util.rxpreferences.GsonOptionalPreferenceConverter
import cc.femto.mvi.Event
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.rxjava2.filterSome
import com.gojuno.koptional.toOptional
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Session @Inject constructor(rxPreferences: RxSharedPreferences, gson: Gson) {

    private val query: Preference<Optional<String>>

    init {
        val stringConverter = GsonOptionalPreferenceConverter<String, Optional<String>>(gson, genericType<String>())

        query = rxPreferences.getObject("query", None, stringConverter)
    }

    fun query(): Observable<SessionEvent.Query> = query.asObservable()
        .filterSome()
        .map { SessionEvent.Query(it) }

    fun setQuery(value: String) = query.set(value.toOptional())
}

sealed class SessionEvent : Event {
    data class Query(val query: String) : SessionEvent()
}
