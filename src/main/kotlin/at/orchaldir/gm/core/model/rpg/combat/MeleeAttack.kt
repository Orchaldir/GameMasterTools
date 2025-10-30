package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.Serializable

@Serializable
data class MeleeAttack(
    val effect: AttackEffect = UndefinedAttackEffect,
    val reach: Reach = UndefinedReach,
    val parrying: Parrying = UndefinedParrying,
) {
    fun contains(type: DamageTypeId) = effect.contains(type)
    fun contains(statistic: StatisticId) = effect.contains(statistic)
}