package at.orchaldir.gm.core.selector.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.UserDefinedWeight

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    equipment: Equipment,
    appearance: Appearance = HumanoidBody(),
) = when (equipment.weight) {
    CalculatedWeight -> calculateWeight(state, config, equipment.data, appearance)
    is UserDefinedWeight -> equipment.weight.weight
}

fun calculateWeight(
    state: State,
    config: CalculateVolumeConfig<Appearance>,
    data: EquipmentData,
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
