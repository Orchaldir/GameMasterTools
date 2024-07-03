package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateItem
import at.orchaldir.gm.core.action.DeleteItem
import at.orchaldir.gm.core.action.UpdateItem
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.EquippedItem
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.selector.canEquip
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ITEM: Reducer<CreateItem, State> = { state, action ->
    state.itemTemplates.require(action.template)
    val item = Item(state.items.nextId, action.template)

    noFollowUps(state.copy(items = state.items.add(item)))
}

val DELETE_ITEM: Reducer<DeleteItem, State> = { state, action ->
    state.items.require(action.id)

    noFollowUps(state.copy(items = state.items.remove(action.id)))
}

val UPDATE_ITEM: Reducer<UpdateItem, State> = { state, action ->
    val item = action.item

    state.items.require(item.id)
    state.itemTemplates.require(item.template)
    if (item.location is EquippedItem) {
        val character = item.location.character
        require(
            state.canEquip(
                character,
                item
            )
        ) { "Character ${character.value()} cannot equip item ${item.id.value()}!" }
    }

    noFollowUps(state.copy(items = state.items.update(item)))
}
