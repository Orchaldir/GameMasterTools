package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.math.Factor
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
) : Population()

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
