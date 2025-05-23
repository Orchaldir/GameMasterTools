package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT_MAP: Reducer<UpdateEquipmentOfCharacter, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)

    validateCharacterEquipment(state, action.map)

    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}

fun validateCharacterEquipment(
    state: State,
    equipmentMap: EquipmentIdMap,
) {
    val occupySlots = mutableSetOf<BodySlot>()

    equipmentMap.getEquipmentWithSlotSets().forEach { (pair, slotSets) ->
        val equipment = state.getEquipmentStorage().getOrThrow(pair.first)
        val allowedSlotSets = equipment.data.slots().getAllBodySlotCombinations()

        state.getColorSchemeStorage().require(pair.second)

        slotSets.forEach { slotSet ->
            // Not sure why allowedSlotSets.contains(slotSet) doesn't work
            val contained = allowedSlotSets.any { allowedSet -> allowedSet == slotSet }
            require(contained) { "Equipment ${equipment.id.value} uses wrong slots!" }

            slotSet.forEach { slot -> require(!occupySlots.contains(slot)) { "Body slot $slot is occupied multiple times!" } }

            occupySlots.addAll(slotSet)
        }
    }
}
