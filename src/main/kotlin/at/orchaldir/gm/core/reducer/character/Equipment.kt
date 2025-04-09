package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.getAllBodySlotCombinations
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT: Reducer<UpdateEquipmentOfCharacter, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)

    action.map.getEquipmentWithSlotSets().forEach { (id, slotSets) ->
        val equipment = state.getEquipmentStorage().getOrThrow(id)
        val allowedSlotSets = equipment.data.slots().getAllBodySlotCombinations()

        slotSets.forEach { slotSet ->
            val contains = allowedSlotSets.contains(slotSet)
            require(contains) { "Equipment ${equipment.id.value} uses wrong slots!" }
        }
    }

    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}
