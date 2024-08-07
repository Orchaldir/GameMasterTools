package at.orchaldir.gm.core.model.fashion

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.OneOrNone
import at.orchaldir.gm.core.model.appearance.SomeOf
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

private val EMPTY = OneOrNone<ItemTemplateId>()

@JvmInline
@Serializable
value class FashionId(val value: Int) : Id<FashionId> {

    override fun next() = FashionId(value + 1)
    override fun value() = value

}

@Serializable
data class Fashion(
    val id: FashionId,
    val name: String = "Fashion ${id.value}",
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val accessories: SomeOf<EquipmentType> = SomeOf(emptySet()),
    val itemRarityMap: Map<EquipmentType, OneOrNone<ItemTemplateId>> = emptyMap(),
) : Element<FashionId> {

    override fun id() = id

    fun getAllItemTemplates() = itemRarityMap
        .values
        .flatMap { it.getValidValues() }


    fun getOptions(type: EquipmentType) = itemRarityMap[type] ?: EMPTY

}