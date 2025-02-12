package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT: Reducer<UpdateEquipmentOfCharacter, State> = { state, action ->
    val occupySlots = mutableSetOf<EquipmentSlot>()

    action.map.map.forEach { (type, id) ->
        val template = state.getEquipmentStorage().getOrThrow(id)
        val slots = template.slots()

        require(type == template.data.getType()) { "Item template ${template.id.value} has wrong type!" }

        slots.forEach { slot -> require(!occupySlots.contains(slot)) { "An Equipment slot $slot is occupied multiple times" } }

        occupySlots.addAll(slots)
    }

    val character = state.getCharacterStorage().getOrThrow(action.id)
    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}
