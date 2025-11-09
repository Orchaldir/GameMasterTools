package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateProtection
import at.orchaldir.gm.utils.Id
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
    val name: Name = Name.init("$SHIELD_TYPE_TYPE ${id.value}"),
    val protection: Protection = UndefinedProtection,
) : ElementWithSimpleName<ShieldTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        validateProtection(state, protection)
    }

    fun contains(type: DamageTypeId) = protection.contains(type)
}