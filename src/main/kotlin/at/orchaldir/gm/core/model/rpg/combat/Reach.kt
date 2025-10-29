package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ReachType {
    Simple,
    Range,
    Undefined,
}

@Serializable
sealed class Reach {

    fun getType() = when (this) {
        is SimpleReach -> ReachType.Simple
        is ReachRange -> ReachType.Range
        is UndefinedReach -> ReachType.Undefined
    }
}

@Serializable
@SerialName("Simple")
data class SimpleReach(
    val distance: Int = 1,
) : Reach()

@Serializable
@SerialName("Range")
data class ReachRange(
    val min: Int = 1,
    val max: Int = 2,
    val changeRequiresEffort: Boolean = false,
) : Reach()

@Serializable
@SerialName("Undefined")
data object UndefinedReach : Reach()
