package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.Serializable

@Serializable
data class RangedAttack(
    val accuracy: Accuracy = UndefinedAccuracy,
    val effect: AttackEffect = UndefinedAttackEffect,
    val range: Range = UndefinedRange,
    val shots: Shots = UndefinedShots,
    val skill: UsedSkill = UndefinedUsedSkill,
) {
    fun contains(type: AmmunitionTypeId) = shots.contains(type)
    fun contains(type: DamageTypeId) = effect.contains(type)
    fun contains(statistic: StatisticId) = effect.contains(statistic) || range.contains(statistic)
}