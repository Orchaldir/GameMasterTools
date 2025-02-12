package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ITEM_TEMPLATE_TYPE = "Item Template"

@JvmInline
@Serializable
value class EquipmentId(val value: Int) : Id<EquipmentId> {

    override fun next() = EquipmentId(value + 1)
    override fun type() = ITEM_TEMPLATE_TYPE
    override fun value() = value

}

@Serializable
data class ItemTemplate(
    val id: EquipmentId,
    val name: String = "Item Template ${id.value}",
    val equipment: EquipmentData = NoEquipment,
) : ElementWithSimpleName<EquipmentId> {

    override fun id() = id
    override fun name() = name

    fun slots() = equipment.slots()

    fun canEquip() = equipment.slots().isNotEmpty()

}