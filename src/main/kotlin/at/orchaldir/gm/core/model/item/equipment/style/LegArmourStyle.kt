package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LegArmourStyleType {
    None,
    Continue,
    Different,
}

@Serializable
sealed class LegArmourStyle : MadeFromParts {

    fun getType() = when (this) {
        is NoLegArmour -> LegArmourStyleType.None
        is ContinueLegArmour -> LegArmourStyleType.Continue
        is DifferentLegArmour -> LegArmourStyleType.Different
    }

    override fun parts() = when (this) {
        is NoLegArmour -> emptyList()
        is ContinueLegArmour -> emptyList()
        is DifferentLegArmour -> style.parts()
    }
}

@Serializable
@SerialName("None")
data object NoLegArmour : LegArmourStyle()

@Serializable
@SerialName("Continue")
data class ContinueLegArmour(
    val length: OuterwearLength = OuterwearLength.Knee,
) : LegArmourStyle()

@Serializable
@SerialName("Different")
data class DifferentLegArmour(
    val style: ArmourStyle,
    val length: OuterwearLength = OuterwearLength.Knee,
) : LegArmourStyle()
