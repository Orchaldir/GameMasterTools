package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PopulationType {
    Abstract,
    Distribution,
    Total,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is AbstractPopulation -> PopulationType.Abstract
        is PopulationDistribution -> PopulationType.Distribution
        is TotalPopulation -> PopulationType.Total
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun getPopulation(race: RaceId) = when (this) {
        is PopulationDistribution -> getNumber(race)
        else -> null
    }

    fun getTotalPopulation() = when (this) {
        is TotalPopulation -> total
        is PopulationDistribution -> total
        is AbstractPopulation, UndefinedPopulation -> null
    }

    fun contains(culture: CultureId) = when (this) {
        is AbstractPopulation -> cultures.contains(culture)
        is PopulationDistribution -> cultures.containsKey(culture)
        is TotalPopulation -> cultures.contains(culture)
        else -> false
    }

    fun contains(race: RaceId) = when (this) {
        is AbstractPopulation -> races.contains(race)
        is PopulationDistribution -> races.containsKey(race)
        is TotalPopulation -> races.contains(race)
        else -> false
    }

    fun cultures() = when (this) {
        is AbstractPopulation -> cultures
        is PopulationDistribution -> cultures.keys
        is TotalPopulation -> cultures
        else -> emptySet()
    }

    fun races() = when (this) {
        is AbstractPopulation -> races
        is PopulationDistribution -> races.keys
        is TotalPopulation -> races
        else -> emptySet()
    }

}

@Serializable
@SerialName("Abstract")
data class AbstractPopulation(
    val density: Size = Size.Medium,
    val races: Set<RaceId> = emptySet(),
    val cultures: Set<CultureId> = emptySet(),
) : Population()

@Serializable
@SerialName("Distribution")
data class PopulationDistribution(
    val total: Int,
    val races: Map<RaceId, Factor> = emptyMap(),
    val cultures: Map<CultureId, Factor> = emptyMap(),
) : Population() {

    fun getPercentage(race: RaceId) = races.getOrDefault(race, ZERO)
    fun getPercentage(culture: CultureId) = cultures.getOrDefault(culture, ZERO)

    fun getNumber(race: RaceId) = getPercentage(race).apply(total)
    fun getNumber(culture: CultureId) = getPercentage(culture).apply(total)

    fun getDefinedPercentagesForRaces() = races.values
        .reduceOrNull { sum, percentage -> sum + percentage } ?: ZERO
    fun getUndefinedPercentagesForRaces() = ONE - getDefinedPercentagesForRaces()

    fun getDefinedPercentagesForCultures() = cultures.values
        .reduceOrNull { sum, percentage -> sum + percentage } ?: ZERO
    fun getUndefinedPercentagesForCultures() = ONE - getDefinedPercentagesForCultures()

}

@Serializable
@SerialName("Total")
data class TotalPopulation(
    val total: Int,
    val races: Set<RaceId> = emptySet(),
    val cultures: Set<CultureId> = emptySet(),
) : Population()

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
