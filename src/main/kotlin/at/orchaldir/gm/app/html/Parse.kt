package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.RarityMap
import io.ktor.http.*

inline fun <reified T : Enum<T>> parseOr(parameters: Parameters, param: String, default: T): T =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) } ?: default

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String): T? =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) }

fun <T> parseRarityMap(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
) = RarityMap(
    parameters.getAll(selectId)
        ?.associate {
            val parts = it.split('-')
            val value = converter(parts[0])
            val rarity = Rarity.valueOf(parts[1])
            Pair(value, rarity)
        } ?: mapOf())