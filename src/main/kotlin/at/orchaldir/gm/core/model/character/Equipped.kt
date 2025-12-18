package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.containsId
import at.orchaldir.gm.core.model.item.equipment.containsScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquippedType {
    Undefined,
    Equipment,
    UseTemplate,
    Uniform,
}

@Serializable
sealed class Equipped {

    fun getType() = when (this) {
        UndefinedEquipped -> EquippedType.Undefined
        is EquippedEquipment -> EquippedType.Equipment
        is EquippedUniform -> EquippedType.Uniform
        is UseEquipmentFromTemplate -> EquippedType.UseTemplate
    }

    fun contains(scheme: ColorSchemeId) = when (this) {
        is EquippedEquipment -> map.containsScheme(scheme)
        else -> false
    }

    fun contains(equipment: EquipmentId) = when (this) {
        is EquippedEquipment -> map.containsId(equipment)
        else -> false
    }

    fun contains(uniform: UniformId) = when (this) {
        is EquippedUniform -> this.uniform == uniform
        else -> false
    }

}

@Serializable
@SerialName("Equipment")
data class EquippedEquipment(
    val map: EquipmentIdMap,
) : Equipped()

@Serializable
@SerialName("Uniform")
data class EquippedUniform(
    val uniform: UniformId,
) : Equipped()

@Serializable
@SerialName("UseTemplate")
data object UseEquipmentFromTemplate : Equipped()

@Serializable
@SerialName("Undefined")
data object UndefinedEquipped : Equipped()
