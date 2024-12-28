package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateStreetTemplate
import at.orchaldir.gm.core.action.DeleteStreetTemplate
import at.orchaldir.gm.core.action.UpdateStreetTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STREET_TEMPLATE: Reducer<CreateStreetTemplate, State> = { state, _ ->
    val template = StreetTemplate(state.getStreetTemplateStorage().nextId)

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().add(template)))
}

val DELETE_STREET_TEMPLATE: Reducer<DeleteStreetTemplate, State> = { state, action ->
    state.getStreetTemplateStorage().require(action.id)
    require(state.canDelete(action.id)) { "Street Template ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().remove(action.id)))
}

val UPDATE_STREET_TEMPLATE: Reducer<UpdateStreetTemplate, State> = { state, action ->
    state.getStreetTemplateStorage().require(action.template.id)

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().update(action.template)))
}