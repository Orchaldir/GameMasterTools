package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class MeleeAttack(
    val effect: AttackEffect = UndefinedAttackEffect,
    val reach: Reach = UndefinedReach,
    val parrying: Parrying = UndefinedParrying,
)