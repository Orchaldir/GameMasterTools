package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

const val EQUIPMENT_MODIFIER_TYPE = "Equipment Modifier"

@JvmInline
@Serializable
value class EquipmentModifierId(val value: Int) : Id<EquipmentModifierId> {

    override fun next() = EquipmentModifierId(value + 1)
    override fun type() = EQUIPMENT_MODIFIER_TYPE
    override fun value() = value

}

@Serializable
data class EquipmentModifier(
    val id: EquipmentModifierId,
    val name: Name = Name.init(id),
    val category: EquipmentModifierCategory = EquipmentModifierCategory.All,
    val effects: List<EquipmentModifierEffect> = emptyList(),
    val cost: Factor = DEFAULT_MODIFIER_COST_FACTOR,
) : ElementWithSimpleName<EquipmentModifierId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        val effectTypes = effects.map { it.getType() }
        require(effectTypes.size == effectTypes.toSet().size) { "Contains a type of effects more than once!" }
        validateCost(cost)
    }
}