package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateStreetTemplate
import at.orchaldir.gm.core.action.DeleteStreetTemplate
import at.orchaldir.gm.core.action.UpdateStreetTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STREET_TEMPLATE: Reducer<CreateStreetTemplate, State> = { state, _ ->
    val template = StreetTemplate(state.getStreetTemplateStorage().nextId)

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().add(template)))
}

val DELETE_STREET_TEMPLATE: Reducer<DeleteStreetTemplate, State> = { state, action ->
    state.getStreetTemplateStorage().require(action.id)
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().remove(action.id)))
}

val UPDATE_STREET_TEMPLATE: Reducer<UpdateStreetTemplate, State> = { state, action ->
    val template = action.template
    state.getStreetTemplateStorage().require(template.id)

    validateStreetTemplate(state, template)

    noFollowUps(state.updateStorage(state.getStreetTemplateStorage().update(template)))
}

fun validateStreetTemplate(
    state: State,
    template: StreetTemplate,
) {
    template.materialCost.materials().forEach { state.getMaterialStorage().require(it) }
}