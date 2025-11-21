package at.orchaldir.gm.core.selector.rpg

import kotlin.collections.flatMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId

fun State.getEquipmentModifierEffects(modifiers: Set<EquipmentModifierId>) = getEquipmentModifierStorage()
    .get(modifiers)
    .flatMap { it.effects }