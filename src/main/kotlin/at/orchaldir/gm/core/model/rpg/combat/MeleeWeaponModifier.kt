package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val MELEE_WEAPON_MODIFIER_TYPE = "Melee Weapon Modifier"

@JvmInline
@Serializable
value class MeleeWeaponModifierId(val value: Int) : Id<MeleeWeaponModifierId> {

    override fun next() = MeleeWeaponModifierId(value + 1)
    override fun type() = MELEE_WEAPON_MODIFIER_TYPE
    override fun value() = value

}

@Serializable
data class MeleeWeaponModifier(
    val id: MeleeWeaponModifierId,
    val name: Name = Name.init("$MELEE_WEAPON_MODIFIER_TYPE ${id.value}"),
) : ElementWithSimpleName<MeleeWeaponModifierId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State)  = doNothing()
}