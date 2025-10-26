package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val MELEE_WEAPON_TYPE = "Melee Weapon"

@JvmInline
@Serializable
value class MeleeWeaponId(val value: Int) : Id<MeleeWeaponId> {

    override fun next() = MeleeWeaponId(value + 1)
    override fun type() = MELEE_WEAPON_TYPE
    override fun value() = value

}

@Serializable
data class MeleeWeapon(
    val id: MeleeWeaponId,
    val name: Name = Name.init("$MELEE_WEAPON_TYPE ${id.value}"),
) : ElementWithSimpleName<MeleeWeaponId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = doNothing()
}