package at.orchaldir.gm.core.model.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.rpg.combat.Protection
import at.orchaldir.gm.core.model.rpg.combat.RangedAttack
import at.orchaldir.gm.core.model.rpg.combat.UndefinedProtection
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.rpg.validateMeleeAttack
import at.orchaldir.gm.core.reducer.rpg.validateProtection
import at.orchaldir.gm.core.reducer.rpg.validateRangedAttack
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import at.orchaldir.gm.utils.math.validateFactor
import kotlinx.serialization.Serializable

const val EQUIPMENT_TYPE_TYPE = "Equipment Type"

@JvmInline
@Serializable
value class EquipmentTypeId(val value: Int) : Id<EquipmentTypeId> {

    override fun next() = EquipmentTypeId(value + 1)
    override fun type() = EQUIPMENT_TYPE_TYPE
    override fun value() = value

}

@Serializable
data class EquipmentType(
    val id: EquipmentTypeId,
    val name: Name = Name.init(id),
    val category: EquipmentCategory = EquipmentCategory.Generic,
    val meleeAttacks: List<MeleeAttack> = emptyList(),
    val rangedAttacks: List<RangedAttack> = emptyList(),
    val protection: Protection = UndefinedProtection,
    val cost: Factor = DEFAULT_TYPE_COST_FACTOR,
    val weight: WeightLookup = UndefinedWeight,
) : ElementWithSimpleName<EquipmentTypeId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        meleeAttacks.forEach { validateMeleeAttack(state, it) }
        rangedAttacks.forEach { validateRangedAttack(state, it) }
        validateProtection(state, protection)
        validateFactor(cost, "Cost", MIN_COST_FACTOR, MAX_COST_FACTOR)
    }

    fun contains(type: AmmunitionTypeId) = rangedAttacks.any { it.contains(type) }
    fun contains(type: DamageTypeId) = protection.contains(type) || meleeAttacks.any { it.contains(type) } || rangedAttacks.any { it.contains(type) }
    fun contains(statistic: StatisticId) = meleeAttacks.any { it.contains(statistic) } || rangedAttacks.any { it.contains(statistic) }

    fun getMaxReach() = meleeAttacks.maxOf { it.reach.getMaxReach() }
}