package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.world.settlement.*
import at.orchaldir.gm.utils.redux.Reducer

val WORLD_REDUCER: Reducer<WorldAction, State> = { state, action ->
    when (action) {
        // settlement's abstract buildings
        is AddAbstractBuilding -> ADD_ABSTRACT_BUILDING(state, action)
        is RemoveAbstractBuilding -> REMOVE_ABSTRACT_BUILDING(state, action)
        // settlement's buildings
        is AddBuilding -> ADD_BUILDING(state, action)
        is UpdateActionLot -> UPDATE_BUILDING_LOT(state, action)
        // settlement's streets
        is AddStreetTile -> ADD_STREET_TILE(state, action)
        is RemoveStreetTile -> REMOVE_STREET_TILE(state, action)
        // settlement's terrain
        is ResizeTerrain -> RESIZE_TERRAIN(state, action)
        is SetTerrainTile -> SET_TERRAIN_TILE(state, action)
    }
}
