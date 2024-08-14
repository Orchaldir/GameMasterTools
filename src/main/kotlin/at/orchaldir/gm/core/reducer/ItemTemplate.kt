package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getEquippedBy
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ITEM_TEMPLATE: Reducer<CreateItemTemplate, State> = { state, _ ->
    val itemTemplate = ItemTemplate(state.getItemTemplateStorage().nextId)

    noFollowUps(state.copy(itemTemplates = state.getItemTemplateStorage().add(itemTemplate)))
}

val DELETE_ITEM_TEMPLATE: Reducer<DeleteItemTemplate, State> = { state, action ->
    state.getItemTemplateStorage().require(action.id)
    require(state.canDelete(action.id)) { "Item ${action.id.value} is used" }

    noFollowUps(state.copy(itemTemplates = state.getItemTemplateStorage().remove(action.id)))
}

val UPDATE_ITEM_TEMPLATE: Reducer<UpdateItemTemplate, State> = { state, action ->
    val template = action.itemTemplate

    val oldTemplate = state.getItemTemplateStorage().getOrThrow(template.id)
    template.equipment.getMaterials().forEach { state.getMaterialStorage().require(it) }

    if (template.equipment.javaClass != oldTemplate.equipment.javaClass) {
        require(
            state.getEquippedBy(template.id).isEmpty()
        ) { "Cannot change item template ${template.id.value} while it is equipped" }
    }

    noFollowUps(state.copy(itemTemplates = state.getItemTemplateStorage().update(template)))
}
