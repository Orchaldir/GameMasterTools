package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Equipped
import at.orchaldir.gm.core.model.character.EquippedEquipment
import at.orchaldir.gm.core.model.character.EquippedUniform
import at.orchaldir.gm.core.model.character.ModifiedUniform
import at.orchaldir.gm.core.model.character.ModifyEquipmentFromTemplate
import at.orchaldir.gm.core.model.character.UndefinedEquipped
import at.orchaldir.gm.core.model.character.UseEquipmentFromTemplate
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.core.model.rpg.statblock.StatblockLookup
import at.orchaldir.gm.core.selector.item.getEquipmentMap
import at.orchaldir.gm.core.selector.item.getEquipmentMapForLookup
import at.orchaldir.gm.utils.doNothing

fun validateEquipped(
    state: State,
    equipped: Equipped,
    lookup: StatblockLookup,
) = when (equipped) {
    is EquippedEquipment -> validateEquipmentMap(state, equipped.map)
    is EquippedUniform -> state.getUniformStorage().require(equipped.uniform)
    is ModifiedUniform -> {
        validateEquipmentMapUpdate(state, state.getEquipmentMap(equipped.uniform), equipped.update)
    }
    UseEquipmentFromTemplate -> validateTemplate(lookup)
    is ModifyEquipmentFromTemplate -> {
        validateTemplate(lookup)
        validateEquipmentMapUpdate(state, state.getEquipmentMapForLookup(lookup), equipped.update)
    }
    UndefinedEquipped -> doNothing()
}

private fun validateTemplate(lookup: StatblockLookup) =
    requireNotNull(lookup.template()) { "Cannot use equipment from the template without a template!" }

fun validateEquipmentMap(
    state: State,
    equipmentMap: EquipmentIdMap,
) {
    val occupySlots = mutableSetOf<BodySlot>()

    equipmentMap.getEquipmentWithSlotSets().forEach { (pair, slotSets) ->
        val equipment = state.getEquipmentStorage().getOrThrow(pair.first)
        val allowedSlotSets = equipment.data.slots().getAllBodySlotCombinations()

        state.getColorSchemeStorage().requireOptional(pair.second)

        slotSets.forEach { slotSet ->
            // Not sure why allowedSlotSets.contains(slotSet) doesn't work
            val contained = allowedSlotSets.any { allowedSet -> allowedSet == slotSet }
            require(contained) { "Equipment ${equipment.id.value} uses wrong slots!" }

            slotSet.forEach { slot -> require(!occupySlots.contains(slot)) { "Body slot $slot is occupied multiple times!" } }

            occupySlots.addAll(slotSet)
        }
    }
}

fun validateEquipmentMapUpdate(
    state: State,
    base: EquipmentIdMap,
    update: EquipmentMapUpdate,
) {
    val updated = update.applyTo(base)

    validateEquipmentMap(state, updated)
}