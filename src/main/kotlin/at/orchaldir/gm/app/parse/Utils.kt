package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.selector.getDefaultCalendar
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

// date

fun parseDate(
    parameters: Parameters,
    state: State,
    param: String,
    default: Date? = null,
): Date = parseDate(parameters, state.getDefaultCalendar(), param, default)

fun parseDate(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
    default: Date? = null,
): Date {
    if (default != null && !parameters.contains(combine(param, ERA))) {
        return default
    }

    return when (parse(parameters, combine(param, DATE), DateType.Year)) {
        DateType.Day -> parseDay(parameters, calendar, param)
        DateType.Year -> parseYear(parameters, calendar, param)
    }
}

fun parseDay(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
    default: Day? = null,
): Day {
    val eraParam = combine(param, ERA)

    if (default != null && !parameters.contains(eraParam)) {
        return default
    }

    val eraIndex = parseInt(parameters, eraParam)
    val yearIndex = parseInt(parameters, combine(param, YEAR), 1) - 1
    val monthIndex = parseInt(parameters, combine(param, MONTH))
    val dayIndex = parseDayIndex(parameters, param)
    val calendarDate = DisplayDay(eraIndex, yearIndex, monthIndex, dayIndex)

    return calendar.resolve(calendarDate)
}

fun parseDayIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, DAY), 1) - 1

fun parseYear(
    parameters: Parameters,
    default: Calendar,
    param: String,
): Year {
    val eraIndex = parseInt(parameters, combine(param, ERA))
    val yearIndex = parseInt(parameters, combine(param, YEAR), 1) - 1
    val calendarDate = DisplayYear(eraIndex, yearIndex)

    return default.resolve(calendarDate)
}
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

fun parseDistribution(parameters: Parameters, param: String) = Distribution(
    parseFloat(parameters, combine(param, CENTER)),
    parseFloat(parameters, combine(param, OFFSET)),
)

fun parseBool(parameters: Parameters, param: String, default: Boolean = false) =
    parameters[param]?.toBoolean() ?: default

fun parseInt(parameters: Parameters, param: String, default: Int = 0) = parameters[param]?.toInt() ?: default

fun parseFactor(parameters: Parameters, param: String, default: Factor = FULL) =
    parameters[param]?.toFloat()?.let { Factor(it) } ?: default

fun parseFloat(parameters: Parameters, param: String, default: Float = 0.0f) = parameters[param]?.toFloat() ?: default

fun parseName(parameters: Parameters, param: String): String? {
    val name = parameters[param]?.trim() ?: return null

    if (name.isEmpty()) {
        return null
    }

    return name
}