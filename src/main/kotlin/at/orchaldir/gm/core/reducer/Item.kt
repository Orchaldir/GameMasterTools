package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateItem
import at.orchaldir.gm.core.action.DeleteItem
import at.orchaldir.gm.core.action.UpdateItem
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ITEM: Reducer<CreateItem, State> = { state, _ ->
    val item = Item(state.items.nextId)
    state.itemTemplates.require(item.template)

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

    noFollowUps(state.copy(items = state.items.update(item)))
}
