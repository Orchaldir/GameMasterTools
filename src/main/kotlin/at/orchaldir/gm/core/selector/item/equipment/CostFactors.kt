package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor


fun calculateCostFactors(
    state: State,
    data: EquipmentData,
): Map<Id<*>, Factor> {
    val map = mutableMapOf<Id<*>, Factor>()

    data.getArmorStats()?.let { calculateCostFactors(state, map, it) }

    return map
}

private fun calculateCostFactors(
    state: State,
    costFactors: MutableMap<Id<*>, Factor>,
    armor: ArmorStats,
) {
    state.getEquipmentModifierStorage()
        .get(armor.modifiers)
        .forEach { modifier ->
            costFactors[modifier.id] = modifier.cost
        }

    state.getArmorTypeStorage().getOptional(armor.type)
        ?.let { costFactors[it.id] = it.cost }
}
