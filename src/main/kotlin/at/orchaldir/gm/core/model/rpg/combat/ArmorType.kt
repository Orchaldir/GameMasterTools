package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateProtection
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

const val ARMOR_TYPE_TYPE = "Armor Type"

@JvmInline
@Serializable
value class ArmorTypeId(val value: Int) : Id<ArmorTypeId> {

    override fun next() = ArmorTypeId(value + 1)
    override fun type() = ARMOR_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class ArmorType(
    val id: ArmorTypeId,
    val name: Name = Name.init(id),
    val protection: Protection = UndefinedProtection,
    val cost: Factor = DEFAULT_TYPE_COST_FACTOR,
) : ElementWithSimpleName<ArmorTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        validateProtection(state, protection)
        validateCost(cost)
    }

    fun contains(type: DamageTypeId) = protection.contains(type)
}