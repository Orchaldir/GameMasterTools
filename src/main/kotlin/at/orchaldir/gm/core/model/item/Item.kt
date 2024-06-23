package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ItemId(val value: Int) : Id<ItemId> {

    override fun next() = ItemId(value + 1)
    override fun value() = value

}

@Serializable
data class Item(
    val id: ItemId,
    val name: String = "Item ${id.value}",
    val template: ItemTemplateId = ItemTemplateId(0),
) : Element<ItemId> {

    override fun id() = id

}