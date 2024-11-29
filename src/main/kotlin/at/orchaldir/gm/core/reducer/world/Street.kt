package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateStreet
import at.orchaldir.gm.core.action.DeleteStreet
import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.reducer.util.checkComplexName
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STREET: Reducer<CreateStreet, State> = { state, _ ->
    val street = Street(state.getStreetStorage().nextId)

    noFollowUps(state.updateStorage(state.getStreetStorage().add(street)))
}

val DELETE_STREET: Reducer<DeleteStreet, State> = { state, action ->
    state.getStreetStorage().require(action.id)
    require(state.canDelete(action.id)) { "Street ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getStreetStorage().remove(action.id)))
}

val UPDATE_STREET: Reducer<UpdateStreet, State> = { state, action ->
    state.getStreetStorage().require(action.street.id)

    checkComplexName(state, action.street.name)

    noFollowUps(state.updateStorage(state.getStreetStorage().update(action.street)))
}