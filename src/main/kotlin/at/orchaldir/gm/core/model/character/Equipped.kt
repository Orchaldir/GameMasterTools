package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquippedType {
    Undefined,
    Equipment,
    ModifyTemplate,
    ModifyUniform,
    UseTemplate,
    UseUniform,
}

@Serializable
sealed class Equipped {

    fun getType() = when (this) {
        UndefinedEquipped -> EquippedType.Undefined
        is EquippedEquipment -> EquippedType.Equipment
        is ModifyEquipmentFromTemplate -> EquippedType.ModifyTemplate
        is ModifyUniform -> EquippedType.ModifyUniform
        is UseEquipmentFromTemplate -> EquippedType.UseTemplate
        is UseUniform -> EquippedType.UseUniform
    }

    fun contains(scheme: ColorSchemeId) = when (this) {
        is EquippedEquipment -> map.containsScheme(scheme)
        is ModifyEquipmentFromTemplate -> update.added.containsScheme(scheme)
        is ModifyUniform -> update.added.containsScheme(scheme)
        else -> false
    }

    fun contains(equipment: EquipmentId) = when (this) {
        is EquippedEquipment -> map.containsId(equipment)
        is ModifyEquipmentFromTemplate -> update.added.containsId(equipment)
        is ModifyUniform -> update.added.containsId(equipment)
        else -> false
    }

    fun contains(uniform: UniformId) = when (this) {
        is ModifyUniform -> this.uniform == uniform
        is UseUniform -> this.uniform == uniform
        else -> false
    }

}

@Serializable
@SerialName("Equipment")
data class EquippedEquipment(
    val map: EquipmentIdMap,
) : Equipped()

@Serializable
@SerialName("UseUniform")
data class UseUniform(
    val uniform: UniformId,
) : Equipped()

@Serializable
@SerialName("ModifyUniform")
data class ModifyUniform(
    val uniform: UniformId,
    val update: EquipmentMapUpdate,
) : Equipped()

@Serializable
@SerialName("UseTemplate")
data object UseEquipmentFromTemplate : Equipped()

@Serializable
@SerialName("ModifyTemplate")
data class ModifyEquipmentFromTemplate(
    val update: EquipmentMapUpdate,
) : Equipped()

@Serializable
@SerialName("Undefined")
data object UndefinedEquipped : Equipped()
