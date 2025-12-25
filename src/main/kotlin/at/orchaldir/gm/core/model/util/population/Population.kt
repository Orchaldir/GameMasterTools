package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.Size
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
        is PopulationDistribution -> cultures.map.containsKey(culture)
        is TotalPopulation -> cultures.contains(culture)
        else -> false
    }

    fun contains(race: RaceId) = when (this) {
        is AbstractPopulation -> races.contains(race)
        is PopulationDistribution -> races.map.containsKey(race)
        is TotalPopulation -> races.contains(race)
        else -> false
    }

    fun cultures() = when (this) {
        is AbstractPopulation -> cultures
        is PopulationDistribution -> cultures.map.keys
        is TotalPopulation -> cultures
        else -> emptySet()
    }

    fun races() = when (this) {
        is AbstractPopulation -> races
        is PopulationDistribution -> races.map.keys
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
    val races: ElementDistribution<RaceId> = ElementDistribution(),
    val cultures: ElementDistribution<CultureId> = ElementDistribution(),
) : Population() {

    fun getNumber(race: RaceId) = races.getNumber(total, race)
    fun getNumber(culture: CultureId) = cultures.getNumber(total, culture)

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
