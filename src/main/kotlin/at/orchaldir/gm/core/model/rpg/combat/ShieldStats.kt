package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class ShieldStats(
    val type: ShieldTypeId? = null,
    val modifiers: Set<EquipmentModifierId> = emptySet(),
)
