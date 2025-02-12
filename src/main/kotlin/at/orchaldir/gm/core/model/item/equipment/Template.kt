package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val EQUIPMENT_TYPE = "Equipment"

@JvmInline
@Serializable
value class EquipmentId(val value: Int) : Id<EquipmentId> {

    override fun next() = EquipmentId(value + 1)
    override fun type() = EQUIPMENT_TYPE
    override fun value() = value

}

@Serializable
data class Equipment(
    val id: EquipmentId,
    val name: String = "Equipment ${id.value}",
    val equipment: EquipmentData = NoEquipment,
) : ElementWithSimpleName<EquipmentId> {

    override fun id() = id
    override fun name() = name

    fun slots() = equipment.slots()

    fun canEquip() = equipment.slots().isNotEmpty()

}