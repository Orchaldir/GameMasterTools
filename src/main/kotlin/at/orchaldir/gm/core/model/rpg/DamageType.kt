package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val DAMAGE_TYPE_TYPE = "Damage Type"

@JvmInline
@Serializable
value class DamageTypeId(val value: Int) : Id<DamageTypeId> {

    override fun next() = DamageTypeId(value + 1)
    override fun type() = DAMAGE_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class DamageType(
    val id: DamageTypeId,
    val name: Name = Name.init("$DAMAGE_TYPE_TYPE ${id.value}"),
    val short: NotEmptyString? = null,
) : ElementWithSimpleName<DamageTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = doNothing()
}