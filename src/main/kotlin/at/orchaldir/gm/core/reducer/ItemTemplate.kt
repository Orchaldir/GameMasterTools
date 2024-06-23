package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ITEM_TEMPLATE: Reducer<CreateItemTemplate, State> = { state, _ ->
    val itemTemplate = ItemTemplate(state.itemTemplates.nextId)

    noFollowUps(state.copy(itemTemplates = state.itemTemplates.add(itemTemplate)))
}

val DELETE_ITEM_TEMPLATE: Reducer<DeleteItemTemplate, State> = { state, action ->
    state.itemTemplates.require(action.id)
    //require(state.canDelete(action.id)) { "Name list ${action.id.value} is used" }

    noFollowUps(state.copy(itemTemplates = state.itemTemplates.remove(action.id)))
}

val UPDATE_ITEM_TEMPLATE: Reducer<UpdateItemTemplate, State> = { state, action ->
    val itemTemplate = action.itemTemplate

    state.itemTemplates.require(itemTemplate.id)

    noFollowUps(state.copy(itemTemplates = state.itemTemplates.update(itemTemplate)))
}
