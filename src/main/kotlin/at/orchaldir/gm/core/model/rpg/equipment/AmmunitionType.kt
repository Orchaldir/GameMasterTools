package at.orchaldir.gm.core.model.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import kotlinx.serialization.Serializable

const val AMMUNITION_TYPE_TYPE = "Ammunition Type"

@JvmInline
@Serializable
value class AmmunitionTypeId(val value: Int) : Id<AmmunitionTypeId> {

    override fun next() = AmmunitionTypeId(value + 1)
    override fun type() = AMMUNITION_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class AmmunitionType(
    val id: AmmunitionTypeId,
    val name: Name = Name.init(id),
    val weight: WeightLookup = UndefinedWeight,
) : ElementWithSimpleName<AmmunitionTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
    }

}