package at.orchaldir.gm.core.model.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.ItemTemplateId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

private val EMPTY = OneOrNone<ItemTemplateId>()
const val FASHION_TYPE = "Fashion"

@JvmInline
@Serializable
value class FashionId(val value: Int) : Id<FashionId> {

    override fun next() = FashionId(value + 1)
    override fun type() = FASHION_TYPE
    override fun value() = value

}

@Serializable
data class Fashion(
    val id: FashionId,
    val name: String = "Fashion ${id.value}",
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val accessories: SomeOf<EquipmentDataType> = SomeOf(emptySet()),
    val itemRarityMap: Map<EquipmentDataType, OneOrNone<ItemTemplateId>> = emptyMap(),
) : ElementWithSimpleName<FashionId> {

    override fun id() = id
    override fun name() = name

    fun getAllItemTemplates() = itemRarityMap
        .values
        .flatMap { it.getValidValues() }


    fun getOptions(type: EquipmentDataType) = itemRarityMap[type] ?: EMPTY

}