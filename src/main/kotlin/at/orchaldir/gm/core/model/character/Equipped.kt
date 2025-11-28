package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquippedType {
    Undefined,
    Equipment,
    Uniform,
}

@Serializable
sealed class Equipped {

    fun getType() = when (this) {
        UndefinedEquipped -> EquippedType.Undefined
        is EquippedEquipment -> EquippedType.Equipment
        is EquippedUniform -> EquippedType.Uniform
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
@SerialName("Undefined")
data object UndefinedEquipped : Equipped()
