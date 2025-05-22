package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
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
    val name: Name = Name.init("Equipment ${id.value}"),
    val data: EquipmentData = Belt(),
    val weight: Weight = Weight.fromGrams(1),
    val colorSchemes: Set<ColorSchemeId> = emptySet(),
) : ElementWithSimpleName<EquipmentId> {

    override fun id() = id
    override fun name() = name.text

    fun slots() = data.slots()

    fun canEquip() = data.slots().isNotEmpty()

}