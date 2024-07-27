package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT: Reducer<UpdateEquipment, State> = { state, action ->
    action.map.map.values.forEach { state.itemTemplates.require(it) }

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}