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
        // town
        is CreateTown -> CREATE_TOWN(state, action)
        is DeleteTown -> DELETE_TOWN(state, action)
        is UpdateTown -> UPDATE_TOWN(state, action)
        is UpdateTerrain -> UPDATE_TERRAIN(state, action)
    }
}
