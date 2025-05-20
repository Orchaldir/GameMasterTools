package at.orchaldir.gm.core.model.realm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarResultType {
    // All sides simply stop fighting without a formal agreement.
    Disengagement,
    Peace,

    // Without one side surrendering.
    TotalVictory,
    Surrender,
    Undefined,
}

@Serializable
sealed class WarResult {

    fun getType() = when (this) {
        Disengagement -> WarResultType.Disengagement
        is Peace -> WarResultType.Peace
        is TotalVictory -> WarResultType.TotalVictory
        is Surrender -> WarResultType.Surrender
        UndefinedWarResult -> WarResultType.Undefined
    }

}

@Serializable
@SerialName("Disengagement")
data object Disengagement : WarResult()

@Serializable
@SerialName("Peace")
data class Peace(val treaty: TreatyId) : WarResult()

@Serializable
@SerialName("TotalVictory")
data object TotalVictory : WarResult()

@Serializable
@SerialName("Surrender")
data class Surrender(val treaty: TreatyId) : WarResult()

@Serializable
@SerialName("Undefined")
data object UndefinedWarResult : WarResult()
