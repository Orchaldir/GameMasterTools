package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.ammunition.Ammunition
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentType
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight
import at.orchaldir.gm.utils.math.unit.WEIGHTLESS
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightBasedOnType
import at.orchaldir.gm.utils.math.unit.WeightLookup

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

fun calculateWeightBasedOnType(state: State, ammunition: Ammunition) = getWeightOfType(state
    .getAmmunitionTypeStorage()
    .getOptional(ammunition.type)?.weight)

fun calculateWeightBasedOnType(state: State, equipment: Equipment): Weight {
    state.getEquipmentType(equipment)?.let { return getWeightOfType(it.weight) }

    return WEIGHTLESS
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
