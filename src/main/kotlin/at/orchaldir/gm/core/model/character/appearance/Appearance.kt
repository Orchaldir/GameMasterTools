package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.EquipmentSlot
import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Appearance

@Serializable
@SerialName("Undefined")
data object UndefinedAppearance : Appearance()

@Serializable
@SerialName("HeadOnly")
data class HeadOnly(
    val head: Head,
    val height: Distance,
) : Appearance()

@Serializable
@SerialName("Humanoid")
data class HumanoidBody(
    val body: Body,
    val head: Head,
    val height: Distance,
) : Appearance()

fun Appearance.getAvailableEquipmentSlots(): Set<EquipmentSlot> = when (this) {
    is HeadOnly -> setOf(EquipmentSlot.Headwear)
    is HumanoidBody -> EquipmentSlot.entries.toSet()
    UndefinedAppearance -> emptySet()
}