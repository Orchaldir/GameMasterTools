package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class MeleeAttack(
    val effect: AttackEffect = UndefinedAttackEffect,
    val parrying: Parrying = UndefinedParrying,
)