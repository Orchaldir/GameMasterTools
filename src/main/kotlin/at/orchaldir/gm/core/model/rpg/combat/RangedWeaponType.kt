package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateRangedAttack
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val RANGED_WEAPON_TYPE_TYPE = "Ranged Weapon Type"

@JvmInline
@Serializable
value class RangedWeaponTypeId(val value: Int) : Id<RangedWeaponTypeId> {

    override fun next() = RangedWeaponTypeId(value + 1)
    override fun type() = RANGED_WEAPON_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class RangedWeaponType(
    val id: RangedWeaponTypeId,
    val name: Name = Name.init(id),
    val attacks: List<RangedAttack> = emptyList(),
) : ElementWithSimpleName<RangedWeaponTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        attacks.forEach { validateRangedAttack(state, it) }
    }

    fun contains(type: AmmunitionTypeId) = attacks.any { it.contains(type) }
    fun contains(type: DamageTypeId) = attacks.any { it.contains(type) }
    fun contains(statistic: StatisticId) = attacks.any { it.contains(statistic) }

}