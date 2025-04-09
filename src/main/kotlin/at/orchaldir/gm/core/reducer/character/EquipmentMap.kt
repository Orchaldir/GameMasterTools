package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT_MAP: Reducer<UpdateEquipmentOfCharacter, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)

    action.map.getEquipmentWithSlotSets().forEach { (id, slotSets) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val allowedSlotSets = equipment.data.slots().getAllBodySlotCombinations()

        slotSets.forEach { slotSet ->
            // Not sure why allowedSlotSets.contains(slotSet) doesn't work
            val contained = allowedSlotSets.any { allowedSet -> allowedSet == slotSet }
            require(contained) { "Equipment ${equipment.id.value} uses wrong slots!" }
        }
    }

    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}
