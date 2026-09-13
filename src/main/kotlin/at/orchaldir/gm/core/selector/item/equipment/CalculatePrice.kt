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
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentType
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial


fun calculatePriceFactors(
    state: State,
    stats: EquipmentStats,
): Map<Id<*>, Factor> {
    val map = mutableMapOf<Id<*>, Factor>()

    calculatePriceFactors(state, map, stats)

    return map
}

private fun calculatePriceFactors(
    state: State,
    priceFactors: MutableMap<Id<*>, Factor>,
    stats: EquipmentStats,
) {
    state.getEquipmentModifierStorage()
        .get(stats.modifiers)
        .forEach { modifier ->
            priceFactors[modifier.id] = modifier.price
        }
}

private fun calculatePriceFactor(priceFactors: Map<Id<*>, Factor>): Factor {
    var factor = FULL

    priceFactors.forEach { modifier ->
        factor += modifier.value
    }

    return factor.max(ZERO)
}

private fun calculatePriceFactor(
    state: State,
    equipment: Equipment,
) = calculatePriceFactor(calculatePriceFactors(state, equipment.stats))

fun calculatePrice(
    state: State,
    vpm: VolumePerMaterial,
    priceFactors: Map<Id<*>, Factor> = emptyMap(),
): Price {
    val materialCost = vpm.getPrice(state)

    if (priceFactors.entries.isEmpty()) {
        return materialCost
    }

    val priceFactor = priceFactors.entries
        .map { it.value }
        .reduce { total, factor -> total + factor }

    return materialCost * priceFactor
}

fun calculatePrice(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.price) {
    CalculatedPrice -> calculatePriceBasedOnAppearance(state, config, equipment.appearance, appearance)
    PriceBasedOnType -> calculatePriceBasedOnType(state, equipment)
    UndefinedPrice -> FREE
    is UserDefinedPrice -> equipment.price.price
}

fun calculatePriceBasedOnAppearance(
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
        return getPriceOfType(it.price) * calculatePriceFactor(state, equipment)
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