package at.orchaldir.gm.core.model.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.model.rpg.combat.UndefinedProtection
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateProtection
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import kotlinx.serialization.Serializable

const val SHIELD_TYPE_TYPE = "Shield Type"

@JvmInline
@Serializable
value class ShieldTypeId(val value: Int) : Id<ShieldTypeId> {

    override fun next() = ShieldTypeId(value + 1)
    override fun type() = SHIELD_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class ShieldType(
    val id: ShieldTypeId,
    val name: Name = Name.init(id),
    val protection: Protection = UndefinedProtection,
    val weight: WeightLookup = UndefinedWeight,
) : ElementWithSimpleName<ShieldTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        validateProtection(state, protection)
    }

    fun contains(type: DamageTypeId) = protection.contains(type)
}