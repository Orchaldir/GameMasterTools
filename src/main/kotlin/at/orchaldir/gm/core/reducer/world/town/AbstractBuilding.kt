package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.AbstractBuildingTile
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_ABSTRACT_BUILDING: Reducer<AddAbstractBuilding, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val map = oldMap.build(action.tileIndex, AbstractBuildingTile)

    noFollowUps(state.updateStorage(state.getTownStorage().update(map)))
}

val REMOVE_ABSTRACT_BUILDING: Reducer<RemoveAbstractBuilding, State> = { state, action ->
    val oldMap = state.getTownMapStorage().getOrThrow(action.town)
    val map = oldMap.removeAbstractBuilding(action.tileIndex)

    noFollowUps(state.updateStorage(state.getTownStorage().update(map)))
}
