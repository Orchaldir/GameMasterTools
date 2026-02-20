package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LegArmourStyleType {
    Same,
    Different,
}

@Serializable
sealed class LegArmourStyle : MadeFromParts {

    fun getType() = when (this) {
        is SameLegArmour -> LegArmourStyleType.Same
        is DifferentLegArmour -> LegArmourStyleType.Different
    }

    fun upperBodyLength() = when (this) {
        is SameLegArmour -> length
        is DifferentLegArmour -> OuterwearLength.Hip
    }

    override fun parts() = when (this) {
        is SameLegArmour -> emptyList()
        is DifferentLegArmour -> style.parts()
    }
}

@Serializable
@SerialName("same")
data class SameLegArmour(
    val length: OuterwearLength = OuterwearLength.Knee,
) : LegArmourStyle()

@Serializable
@SerialName("Different")
data class DifferentLegArmour(
    val style: ArmourStyle,
    val length: OuterwearLength = OuterwearLength.Knee,
) : LegArmourStyle()
