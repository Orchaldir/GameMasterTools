package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
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
    val type: AmmunitionTypeId,
    val name: Name = Name.init(id),
    val modifiers: Set<EquipmentModifierId> = emptySet(),
) : ElementWithSimpleName<AmmunitionId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
    }

}