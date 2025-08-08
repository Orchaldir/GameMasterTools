package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.race.RaceId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PopulationType {
    Simple,
    Undefined,
}

@Serializable
sealed class Population {

    fun getType() = when (this) {
        is SimplePopulation -> PopulationType.Simple
        UndefinedPopulation -> PopulationType.Undefined
    }

    fun calculateTotalPopulation() = when (this) {
        is SimplePopulation -> raceMap
            .map { it.value }
            .sum()

        UndefinedPopulation -> null
    }

}

@Serializable
@SerialName("Simple")
data class SimplePopulation(
    val raceMap: Map<RaceId, UInt>,
) : Population()

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
