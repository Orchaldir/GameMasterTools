package at.orchaldir.gm.core.selector.rpg.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentModifierId

fun State.getEquipmentModifierEffects(modifiers: Set<EquipmentModifierId>) = getEquipmentModifierStorage()
    .get(modifiers)
    .flatMap { it.effects }