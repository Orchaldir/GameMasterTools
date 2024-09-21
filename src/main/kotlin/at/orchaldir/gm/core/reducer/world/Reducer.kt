package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val WORLD_REDUCER: Reducer<WorldAction, State> = { state, action ->
    when (action) {
        // moon
        is CreateMoon -> CREATE_MOON(state, action)
        is DeleteMoon -> DELETE_MOON(state, action)
        is UpdateMoon -> UPDATE_MOON(state, action)
        // moon
        is CreateMountain -> CREATE_MOUNTAIN(state, action)
        is DeleteMountain -> DELETE_MOUNTAIN(state, action)
        is UpdateMountain -> UPDATE_MOUNTAIN(state, action)
        // river
        is CreateRiver -> CREATE_RIVER(state, action)
        is DeleteRiver -> DELETE_RIVER(state, action)
        is UpdateRiver -> UPDATE_RIVER(state, action)
        // street
        is CreateStreet -> CREATE_STREET(state, action)
        is DeleteStreet -> DELETE_STREET(state, action)
        is UpdateStreet -> UPDATE_STREET(state, action)
        // town
        is CreateTown -> CREATE_TOWN(state, action)
        is DeleteTown -> DELETE_TOWN(state, action)
        is UpdateTown -> UPDATE_TOWN(state, action)
        // town's buildings
        is AddBuilding -> ADD_BUILDING(state, action)
        is DeleteBuilding -> DELETE_BUILDING(state, action)
        // town's streets
        is AddStreetTile -> ADD_STREET_TILE(state, action)
        is RemoveStreetTile -> REMOVE_STREET_TILE(state, action)
        // town's terrain
        is SetTerrainTile -> SET_TERRAIN_TILE(state, action)
    }
}
