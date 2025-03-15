package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.Weight
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
    val data: EquipmentData = NoEquipment,
    val weight: Weight = Weight.fromGram(1),
) : ElementWithSimpleName<EquipmentId> {

    override fun id() = id
    override fun name() = name

    fun slots() = data.slots()

    fun canEquip() = data.slots().isNotEmpty()

}