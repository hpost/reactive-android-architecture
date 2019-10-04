package cc.femto.common.util.rxpreferences

import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * A [Preference.Converter] implementation that serializes types as JSON strings
 */
class GsonPreferenceConverter<T>(private val gson: Gson, private val type: Type) : Preference.Converter<T> {

    override fun serialize(value: T): String = gson.toJson(value)

    override fun deserialize(serialized: String): T = gson.fromJson<T>(serialized, type)
}
