package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.EquipmentSlot
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_EQUIPMENT: Reducer<UpdateEquipment, State> = { state, action ->
    val occupySlots = mutableSetOf<EquipmentSlot>()

    action.map.map.forEach { (type, id) ->
        val template = state.itemTemplates.getOrThrow(id)
        val slots = template.slots()

        require(type == template.equipment.getType()) { "Item template ${template.id.value} has wrong type!" }

        slots.forEach { slot -> require(!occupySlots.contains(slot)) { "An Equipment slot $slot is occupied multiple times" } }

        occupySlots.addAll(slots)
    }

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(equipmentMap = action.map)

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}
