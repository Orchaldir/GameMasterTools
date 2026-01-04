package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial


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

fun calculatePrice(
    state: State,
    vpm: VolumePerMaterial,
    costFactors: Map<Id<*>, Factor> = emptyMap(),
): Price {
    val materialCost = vpm.getPrice(state)
    val totalCostFactor = costFactors.entries
        .map { it.value }
        .reduce { total, factor -> total + factor }

    return materialCost * totalCostFactor
}
