package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_STREET_TILE: Reducer<AddStreetTile, State> = { state, action ->
    state.getStreetTemplateStorage().require(action.type)

    state.getStreetStorage().requireOptional(action.street)

    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val map = oldMap.build(action.tileIndex, StreetTile(action.type, action.street))

    noFollowUps(state.updateStorage(state.getTownMapStorage().update(map)))
}

val REMOVE_STREET_TILE: Reducer<RemoveStreetTile, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val map = oldMap.removeStreet(action.tileIndex)

    noFollowUps(state.updateStorage(state.getTownMapStorage().update(map)))
}
