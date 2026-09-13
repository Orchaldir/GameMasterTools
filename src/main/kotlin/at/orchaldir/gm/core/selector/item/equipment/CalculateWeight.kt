package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentType
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.*

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.weight) {
    CalculatedWeight -> calculateWeight(state, config, equipment.appearance, appearance)
    is UserDefinedWeight -> equipment.weight.weight
    WeightBasedOnType -> calculateWeightBasedOnType(state, equipment)
    UndefinedWeight -> WEIGHTLESS
}

fun calculateWeightBasedOnType(state: State, ammunition: Ammunition) = getWeightOfType(
    state
        .getAmmunitionTypeStorage()
        .getOptional(ammunition.type)?.weight
)

fun calculateWeightBasedOnType(state: State, equipment: Equipment): Weight {
    state.getEquipmentType(equipment)?.let {
        return getWeightOfType(it.weight) * calculateWeightFactor(state, equipment)
    }

    return WEIGHTLESS
}

private fun calculateWeightFactor(
    state: State,
    equipment: Equipment,
): Factor {
    var factor = FULL

    calculateWeightFactors(state, equipment)
        .forEach { modifier ->
            factor += modifier.value
        }

    return factor.max(ZERO)
}

fun calculateWeightFactors(
    state: State,
    equipment: Equipment,
): Map<Id<*>, Factor> {
    val map = mutableMapOf<Id<*>, Factor>()

    calculateWeightFactors(state, map, equipment)

    return map
}

private fun calculateWeightFactors(
    state: State,
    costFactors: MutableMap<Id<*>, Factor>,
    equipment: Equipment,
) {
    state.getEquipmentModifierStorage()
        .get(equipment.stats.modifiers)
        .forEach { modifier ->
            costFactors[modifier.id] = modifier.cost
        }
}

fun getWeightOfType(lookup: WeightLookup?) = when (lookup) {
    CalculatedWeight -> error("Type doesn't support calculating the weight!")
    UndefinedWeight -> WEIGHTLESS
    is UserDefinedWeight -> lookup.weight
    WeightBasedOnType -> error("Type doesn't support WeightBasedOnType!")
    null -> WEIGHTLESS
}

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentAppearance,
    appearance: Appearance = HumanoidBody(),
) = calculateVolumePerMaterial(config, data, appearance)
    .getWeight(state)

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    map: EquipmentIdMap,
    appearance: Appearance = HumanoidBody(),
) = map.getAllEquipment()
    .map { (id, _) -> state.getEquipmentStorage().getOrThrow(id) }
    .map { equipment -> calculateWeight(state, config, equipment, appearance) }
    .reduceOrNull { total, weight -> total + weight }
