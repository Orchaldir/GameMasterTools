package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class MeleeAttack(
    val amount: DamageAmount,
)