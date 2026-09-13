package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.economy.money.CalculatedPrice
import at.orchaldir.gm.core.model.economy.money.FREE
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.economy.money.PriceBasedOnType
import at.orchaldir.gm.core.model.economy.money.PriceLookup
import at.orchaldir.gm.core.model.economy.money.UndefinedPrice
import at.orchaldir.gm.core.model.economy.money.UserDefinedPrice
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.selector.rpg.equipment.getAmmunitionType
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentType
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.utils.math.unit.WEIGHTLESS
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightBasedOnType
import at.orchaldir.gm.utils.math.unit.WeightLookup
import io.ktor.websocket.Frame


fun calculateCostFactors(
    state: State,
    stats: EquipmentStats,
): Map<Id<*>, Factor> {
    val map = mutableMapOf<Id<*>, Factor>()

    calculateCostFactors(state, map, stats)

    return map
}

private fun calculateCostFactors(
    state: State,
    costFactors: MutableMap<Id<*>, Factor>,
    stats: EquipmentStats,
) {
    state.getEquipmentModifierStorage()
        .get(stats.modifiers)
        .forEach { modifier ->
            costFactors[modifier.id] = modifier.price
        }
}

fun calculatePrice(
    state: State,
    vpm: VolumePerMaterial,
    costFactors: Map<Id<*>, Factor> = emptyMap(),
): Price {
    val materialCost = vpm.getPrice(state)

    if (costFactors.entries.isEmpty()) {
        return materialCost
    }

    val totalCostFactor = costFactors.entries
        .map { it.value }
        .reduce { total, factor -> total + factor }

    return materialCost * totalCostFactor
}

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.price) {
    CalculatedPrice -> calculatePrice(state, config, equipment.appearance, appearance)
    PriceBasedOnType -> calculatePriceBasedOnType(state, equipment)
    UndefinedPrice -> FREE
    is UserDefinedPrice -> equipment.price.price
}

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentAppearance,
    appearance: Appearance = HumanoidBody(),
) = calculateVolumePerMaterial(config, data, appearance)
    .getPrice(state)

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    map: EquipmentIdMap,
    appearance: Appearance = HumanoidBody(),
) = map.getAllEquipment()
    .map { (id, _) -> state.getEquipmentStorage().getOrThrow(id) }
    .map { equipment -> calculatePrice(state, config, equipment, appearance) }
    .reduceOrNull { total, price -> total + price }

fun calculatePriceBasedOnType(state: State, ammunition: Ammunition): Price {
    return FREE
}

fun calculatePriceBasedOnType(state: State, equipment: Equipment): Price {
    state.getEquipmentType(equipment)?.let {
        return getPriceOfType(it.price)
    }

    return FREE
}

fun getPriceOfType(lookup: PriceLookup?) = when (lookup) {
    CalculatedPrice -> error("Type doesn't support CalculatedPrice!")
    PriceBasedOnType -> error("Type doesn't support PriceBasedOnType!")
    UndefinedPrice -> FREE
    is UserDefinedPrice -> lookup.price
    null -> FREE
}