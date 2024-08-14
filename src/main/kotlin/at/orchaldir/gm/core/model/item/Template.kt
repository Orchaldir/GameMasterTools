package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ITEM_TEMPLATE = "Item Template"

@JvmInline
@Serializable
value class ItemTemplateId(val value: Int) : Id<ItemTemplateId> {

    override fun next() = ItemTemplateId(value + 1)
    override fun type() = ITEM_TEMPLATE
    override fun value() = value

}

@Serializable
data class ItemTemplate(
    val id: ItemTemplateId,
    val name: String = "Item Template ${id.value}",
    val equipment: Equipment = NoEquipment,
) : Element<ItemTemplateId> {

    override fun id() = id

    fun slots() = equipment.slots()

    fun canEquip() = equipment.slots().isNotEmpty()

}