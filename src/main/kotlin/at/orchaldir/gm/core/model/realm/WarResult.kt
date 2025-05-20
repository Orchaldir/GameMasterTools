package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarResultType {
    Catastrophe,
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
        is InterruptedByCatastrophe -> WarResultType.Catastrophe
        Disengagement -> WarResultType.Disengagement
        is Peace -> WarResultType.Peace
        is TotalVictory -> WarResultType.TotalVictory
        is Surrender -> WarResultType.Surrender
        UndefinedWarResult -> WarResultType.Undefined
    }

    fun treaty() = when (this) {
        is InterruptedByCatastrophe -> treaty
        Disengagement -> null
        is Peace -> treaty
        is Surrender -> treaty
        TotalVictory -> null
        UndefinedWarResult -> null
    }

    fun isEndedBy(catastrophe: CatastropheId) = when (this) {
        is InterruptedByCatastrophe -> this.catastrophe == catastrophe
        else -> false
    }

}

@Serializable
@SerialName("Catastrophe")
data class InterruptedByCatastrophe(
    val catastrophe: CatastropheId,
    val treaty: TreatyId?,
) : WarResult()

@Serializable
@SerialName("Disengagement")
data object Disengagement : WarResult()

@Serializable
@SerialName("Peace")
data class Peace(
    val treaty: TreatyId? = null,
) : WarResult()

@Serializable
@SerialName("TotalVictory")
data object TotalVictory : WarResult()

@Serializable
@SerialName("Surrender")
data class Surrender(
    val treaty: TreatyId? = null,
) : WarResult()

@Serializable
@SerialName("Undefined")
data object UndefinedWarResult : WarResult()
