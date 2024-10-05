package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STREET_TYPE: Reducer<CreateStreetType, State> = { state, _ ->
    val type = StreetType(state.getStreetTypeStorage().nextId)

    noFollowUps(state.updateStorage(state.getStreetTypeStorage().add(type)))
}

val DELETE_STREET_TYPE: Reducer<DeleteStreetType, State> = { state, action ->
    state.getStreetTypeStorage().require(action.id)
    require(state.canDelete(action.id)) { "Street Type ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getStreetTypeStorage().remove(action.id)))
}

val UPDATE_STREET_TYPE: Reducer<UpdateStreetType, State> = { state, action ->
    state.getStreetTypeStorage().require(action.type.id)

    noFollowUps(state.updateStorage(state.getStreetTypeStorage().update(action.type)))
}