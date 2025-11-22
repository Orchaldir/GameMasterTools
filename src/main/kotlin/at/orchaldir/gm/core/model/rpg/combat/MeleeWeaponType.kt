package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateMeleeAttack
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val MELEE_WEAPON_TYPE_TYPE = "Melee Weapon Type"

@JvmInline
@Serializable
value class MeleeWeaponTypeId(val value: Int) : Id<MeleeWeaponTypeId> {

    override fun next() = MeleeWeaponTypeId(value + 1)
    override fun type() = MELEE_WEAPON_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class MeleeWeaponType(
    val id: MeleeWeaponTypeId,
    val name: Name = Name.init("$MELEE_WEAPON_TYPE_TYPE ${id.value}"),
    val attacks: List<MeleeAttack> = emptyList(),
) : ElementWithSimpleName<MeleeWeaponTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        attacks.forEach { validateMeleeAttack(state, it) }
    }

    fun contains(type: DamageTypeId) = attacks.any { it.contains(type) }
    fun contains(statistic: StatisticId) = attacks.any { it.contains(statistic) }

    fun apply(effects: List<EquipmentModifierEffect>) = attacks.map { original ->
        var attack = original

        effects.forEach { effect ->
            attack = effect.modify(attack)
        }

        attack
    }
}