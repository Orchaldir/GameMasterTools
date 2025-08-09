package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.utils.math.Factor
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

    fun getTotalPopulation() = when (this) {
        is SimplePopulation -> total
        UndefinedPopulation -> null
    }

}

@Serializable
@SerialName("Simple")
data class SimplePopulation(
    val total: Int,
    val racePercentages: Map<RaceId, Factor>,
) : Population()

@Serializable
@SerialName("Undefined")
data object UndefinedPopulation : Population()
