package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Distribution
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
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

fun parseFill(parameters: Parameters): Fill {
    val type = parse(parameters, combine(FILL, TYPE), FillType.Solid)

    return when (type) {
        FillType.Solid -> Solid(parse(parameters, combine(FILL, COLOR, 0), Color.SkyBlue))
        FillType.VerticalStripes -> VerticalStripes(
            parse(parameters, combine(FILL, COLOR, 0), Color.Black),
            parse(parameters, combine(FILL, COLOR, 1), Color.White),
            parseWidth(parameters),
        )

        FillType.HorizontalStripes -> HorizontalStripes(
            parse(parameters, combine(FILL, COLOR, 0), Color.Black),
            parse(parameters, combine(FILL, COLOR, 1), Color.White),
            parseWidth(parameters),
        )

        FillType.Tiles -> Tiles(
            parse(parameters, combine(FILL, COLOR, 0), Color.Black),
            parse<Color>(parameters, combine(FILL, COLOR, 1)),
            parseFloat(parameters, combine(PATTERN, TILE), 1.0f),
            parseFactor(parameters, combine(PATTERN, BORDER), Factor(0.1f))
        )
    }
}

private fun parseWidth(parameters: Parameters) = parseUByte(parameters, combine(PATTERN, WIDTH), 1u)

//

fun parseDistribution(parameters: Parameters, param: String) = Distribution(
    parseDistance(parameters, combine(param, CENTER)),
    parseDistance(parameters, combine(param, OFFSET)),
)

fun parseBool(parameters: Parameters, param: String, default: Boolean = false) =
    parameters[param]?.toBoolean() ?: default

fun parseInt(parameters: Parameters, param: String, default: Int = 0) = parameters[param]?.toInt() ?: default

fun parseOptionalInt(parameters: Parameters, param: String): Int? {
    val value = parameters[param]

    if (value.isNullOrEmpty()) {
        return null
    }

    return value.toInt()
}

fun parseUByte(parameters: Parameters, param: String, default: UByte = 0u) = parameters[param]?.toUByte() ?: default

fun parseFactor(parameters: Parameters, param: String, default: Factor = FULL) =
    parameters[param]?.toFloat()?.let { Factor(it) } ?: default

fun parseFloat(parameters: Parameters, param: String, default: Float = 0.0f) = parameters[param]?.toFloat() ?: default

fun parseString(parameters: Parameters, param: String, default: String = "") = parameters[param]?.trim() ?: default

fun parseOptionalString(parameters: Parameters, param: String) = parameters[param]?.ifEmpty { null }?.trim()

fun <ID : Id<ID>> parseElements(parameters: Parameters, param: String, parseId: (String) -> ID) =
    parameters.getAll(param)
        ?.map { parseId(it) }
        ?.toSet()
        ?: emptySet()