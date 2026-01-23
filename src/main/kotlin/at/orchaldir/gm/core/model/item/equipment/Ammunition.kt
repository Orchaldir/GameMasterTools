package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CalculatedPrice
import at.orchaldir.gm.core.model.economy.money.PriceLookup
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import kotlinx.serialization.Serializable

const val AMMUNITION_TYPE = "Ammunition"

@JvmInline
@Serializable
value class AmmunitionId(val value: Int) : Id<AmmunitionId> {

    override fun next() = AmmunitionId(value + 1)
    override fun type() = AMMUNITION_TYPE
    override fun value() = value

}

@Serializable
data class Ammunition(
    val id: AmmunitionId,
    val name: Name = Name.init(id),
    val type: AmmunitionTypeId = AmmunitionTypeId(0),
    val modifiers: Set<EquipmentModifierId> = emptySet(),
    val weight: WeightLookup = CalculatedWeight,
    val price: PriceLookup = CalculatedPrice,
) : ElementWithSimpleName<AmmunitionId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
    }

}