package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class ArmorStats(
    val type: ArmorTypeId? = null,
    val modifiers: Set<ArmorModifierId> = emptySet(),
)
