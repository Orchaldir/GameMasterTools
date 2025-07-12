package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.utils.Id
import io.ktor.http.*

fun combine(param0: String, param1: String) = "$param0-$param1"
fun combine(param0: String, param1: String, param2: String) = "$param0-$param1-$param2"
fun combine(param: String, number: Int) = combine(param, number.toString())
fun combine(param0: String, param1: String, number: Int) = combine(param0, param1, number.toString())

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String, default: T): T =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) } ?: default

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String): T? =
    parameters[param]
        ?.let {
            if (it == "") {
                null
            } else {
                java.lang.Enum.valueOf(T::class.java, it)
            }
        }

inline fun <reified T : Enum<T>> parseSet(parameters: Parameters, param: String): Set<T> =
    parameters.getAll(param)?.map { java.lang.Enum.valueOf(T::class.java, it) }?.toSet() ?: emptySet()


// RarityMap

fun <T> parseOneOf(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): OneOf<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return OneOf.init(map)
    }

    return OneOf(default)
}

fun <T> parseOneOrNone(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): OneOrNone<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return OneOrNone.init(map)
    }

    return OneOrNone(default)
}

fun <T> parseSomeOf(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): SomeOf<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return SomeOf.init(map)
    }

    return SomeOf(default)
}

private fun <T> parseRarityMap(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
) = parameters.getAll(selectId)
    ?.associate {
        val parts = it.split('-')
        val value = converter(parts[0])
        val rarity = Rarity.valueOf(parts[1])
        Pair(value, rarity)
    }

//

fun <T> parseElements(parameters: Parameters, param: String, parseId: (String) -> T) =
    parameters.getAll(param)
        ?.map { parseId(it) }
        ?.toSet()
        ?: emptySet()