package at.orchaldir.gm.core.model.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.validateFactor
import kotlinx.serialization.Serializable

val MIN_WEIGHT_FACTOR = Factor.fromPercentage(-100)
val DEFAULT_WEIGHT_FACTOR = ZERO
val MAX_WEIGHT_FACTOR = Factor.fromNumber(100)

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
    val category: EquipmentCategory = EquipmentCategory.Generic,
    val effects: List<EquipmentModifierEffect> = emptyList(),
    val cost: Factor = DEFAULT_MODIFIER_COST_FACTOR,
    val weight: Factor = DEFAULT_WEIGHT_FACTOR,
) : ElementWithSimpleName<EquipmentModifierId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        val effectTypes = effects.map { it.getType() }
        require(effectTypes.size == effectTypes.toSet().size) { "Contains a type of effects more than once!" }
        validateFactor(cost, "Cost", MIN_COST_FACTOR, MAX_COST_FACTOR)
        validateFactor(weight, "Weight", MIN_WEIGHT_FACTOR, MAX_WEIGHT_FACTOR)
    }
}