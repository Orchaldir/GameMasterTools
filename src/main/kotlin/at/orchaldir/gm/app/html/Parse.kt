package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.RarityMap
import io.ktor.http.*

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String, default: T): T =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) } ?: default

fun <T> parseRarityMap(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): RarityMap<T> {
    val map = parameters.getAll(selectId)
        ?.associate {
            val parts = it.split('-')
            val value = converter(parts[0])
            val rarity = Rarity.valueOf(parts[1])
            Pair(value, rarity)
        }

    if (map != null) {
        return RarityMap.init(map)
    }

    return RarityMap(default)
}