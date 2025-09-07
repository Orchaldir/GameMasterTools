package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateStreet
import at.orchaldir.gm.core.action.DeleteStreet
import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STREET: Reducer<CreateStreet, State> = { state, _ ->
    val street = Street(state.getStreetStorage().nextId)

    noFollowUps(state.updateStorage(state.getStreetStorage().add(street)))
}

val UPDATE_STREET: Reducer<UpdateStreet, State> = { state, action ->
    state.getStreetStorage().require(action.street.id)

    noFollowUps(state.updateStorage(state.getStreetStorage().update(action.street)))
}