package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PopulationType {
    Total,
    PerRace,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is TotalPopulation -> PopulationType.Total
        is PopulationPerRace -> PopulationType.PerRace
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun getTotalPopulation() = when (this) {
        is TotalPopulation -> total
        is PopulationPerRace -> total
        UndefinedPopulation -> null
    }

}

@Serializable
@SerialName("Total")
data class TotalPopulation(
    val total: Int,
) : Population()

@Serializable
@SerialName("Race")
data class PopulationPerRace(
    val total: Int,
    val racePercentages: Map<RaceId, Factor>,
) : Population() {

    fun getPercentage(race: RaceId) = racePercentages.getOrDefault(race, ZERO)

    fun getDefinedPercentage() = Factor.fromPermyriad(racePercentages.values.sumOf { it.toPermyriad() })
    fun getUndefinedPercentage() = ONE - getDefinedPercentage()

}

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
