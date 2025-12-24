package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PopulationType {
    Abstract,
    PerRace,
    Total,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is AbstractPopulation -> PopulationType.Abstract
        is PopulationPerRace -> PopulationType.PerRace
        is TotalPopulation -> PopulationType.Total
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun getPopulation(race: RaceId) = when (this) {
        is PopulationPerRace -> getNumber(race)
        else -> null
    }

    fun getTotalPopulation() = when (this) {
        is TotalPopulation -> total
        is PopulationPerRace -> total
        is AbstractPopulation, UndefinedPopulation -> null
    }

    fun contains(race: RaceId) = when (this) {
        is AbstractPopulation -> races.contains(race)
        is PopulationPerRace -> racePercentages.containsKey(race)
        is TotalPopulation -> races.contains(race)
        else -> false
    }

}

@Serializable
@SerialName("Abstract")
data class AbstractPopulation(
    val density: Size = Size.Medium,
    val races: Set<RaceId> = emptySet(),
) : Population()

@Serializable
@SerialName("Race")
data class PopulationPerRace(
    val total: Int,
    val racePercentages: Map<RaceId, Factor>,
) : Population() {

    fun getPercentage(race: RaceId) = racePercentages.getOrDefault(race, ZERO)
    fun getNumber(race: RaceId) = getPercentage(race).apply(total)

    fun getDefinedPercentage() = Factor.fromPermyriad(racePercentages.values.sumOf { it.toPermyriad() })
    fun getUndefinedPercentage() = ONE - getDefinedPercentage()

}

@Serializable
@SerialName("Total")
data class TotalPopulation(
    val total: Int,
    val races: Set<RaceId> = emptySet(),
) : Population()

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
