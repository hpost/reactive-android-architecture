package cc.femto.common.util.rxpreferences

import com.f2prateek.rx.preferences2.Preference
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * A [Preference.Converter] implementation that serializes [Optional] types as JSON strings
 *
 * @param typeOfS The [Type] of the type wrapped inside an [Optional]
 */
class GsonOptionalPreferenceConverter<S, T : Optional<*>>(private val gson: Gson, private val typeOfS: Type) :
    Preference.Converter<T> {

    override fun serialize(value: T): String = gson.toJson(value.toNullable())

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(serialized: String): T = (gson.fromJson<S>(serialized, typeOfS) as Any?).toOptional() as T
}
