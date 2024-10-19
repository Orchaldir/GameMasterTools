package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_STREET_TILE: Reducer<AddStreetTile, State> = { state, action ->
    state.getStreetStorage().require(action.street)

    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.buildStreet(action.tileIndex, action.street, action.connection)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

val REMOVE_STREET_TILE: Reducer<RemoveStreetTile, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.removeStreet(action.tileIndex, action.street)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}