package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ItemTemplateId(val value: Int) : Id<ItemTemplateId> {

    override fun next() = ItemTemplateId(value + 1)
    override fun value() = value

}

@Serializable
data class ItemTemplate(
    val id: ItemTemplateId,
    val name: String = "Item Template ${id.value}",
) : Element<ItemTemplateId> {

    override fun id() = id

}