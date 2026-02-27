package at.orchaldir.gm.core.model.realm.population

import at.orchaldir.gm.core.model.realm.SettlementSizeId
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TotalPopulationType {
    Density,
    Number,
    SettlementSize,
}

@Serializable
sealed class TotalPopulation {

    fun getType() = when (this) {
        is TotalPopulationAsDensity -> TotalPopulationType.Density
        is TotalPopulationAsNumber -> TotalPopulationType.Number
        is TotalPopulationAsSettlementSize -> TotalPopulationType.SettlementSize
    }

    fun getTotal() = when (this) {
        is TotalPopulationAsDensity -> null
        is TotalPopulationAsNumber -> number
        is TotalPopulationAsSettlementSize -> null
    }

    fun isSize(size: SettlementSizeId) = when (this) {
        is TotalPopulationAsSettlementSize -> id == size
        else -> false
    }

}

@Serializable
@SerialName("Density")
data class TotalPopulationAsDensity(
    val density: Size = Size.Medium,
) : TotalPopulation()

@Serializable
@SerialName("Number")
data class TotalPopulationAsNumber(
    val number: Int,
) : TotalPopulation()

@Serializable
@SerialName("SettlementSize")
data class TotalPopulationAsSettlementSize(
    val id: SettlementSizeId,
) : TotalPopulation()
