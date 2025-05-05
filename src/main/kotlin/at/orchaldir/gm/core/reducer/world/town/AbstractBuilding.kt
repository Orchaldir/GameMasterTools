package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.AbstractBuildingTile
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_ABSTRACT_BUILDING: Reducer<AddAbstractBuilding, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.build(action.tileIndex, AbstractBuildingTile)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

val REMOVE_ABSTRACT_BUILDING: Reducer<RemoveAbstractBuilding, State> = { state, action ->
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.removeAbstractBuilding(action.tileIndex)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}
