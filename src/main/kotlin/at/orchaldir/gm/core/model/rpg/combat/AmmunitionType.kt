package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
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
) : ElementWithSimpleName<AmmunitionTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
    }

}