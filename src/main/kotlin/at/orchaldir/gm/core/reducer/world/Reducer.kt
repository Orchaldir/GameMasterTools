package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.reducer.world.town.*
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.redux.Reducer

val WORLD_REDUCER: Reducer<WorldAction, State> = { state, action ->
    when (action) {
        // architectural style
        is DeleteArchitecturalStyle -> deleteElement(state, action.id, State::canDeleteArchitecturalStyle)
        // moon
        is DeleteMoon -> deleteElement(state, action.id, State::canDeleteMoon)
        // plane
        is DeletePlane -> deleteElement(state, action.id, State::canDeletePlane)
        // region
        is DeleteRegion -> deleteElement(state, action.id, State::canDeleteRegion)
        // river
        is DeleteRiver -> deleteElement(state, action.id, State::canDeleteRiver)
        // street
        is DeleteStreet -> deleteElement(state, action.id, State::canDeleteStreet)
        // street template
        is DeleteStreetTemplate -> deleteElement(state, action.id, State::canDeleteStreetTemplate)
        // town
        is DeleteTownMap -> deleteElement(state, action.id, State::canDeleteTownMap)
        // town's abstract buildings
        is AddAbstractBuilding -> ADD_ABSTRACT_BUILDING(state, action)
        is RemoveAbstractBuilding -> REMOVE_ABSTRACT_BUILDING(state, action)
        // town's buildings
        is AddBuilding -> ADD_BUILDING(state, action)
        is DeleteBuilding -> DELETE_BUILDING(state, action)
        is UpdateActionLot -> UPDATE_BUILDING_LOT(state, action)
        // town's streets
        is AddStreetTile -> ADD_STREET_TILE(state, action)
        is RemoveStreetTile -> REMOVE_STREET_TILE(state, action)
        // town's terrain
        is ResizeTerrain -> RESIZE_TERRAIN(state, action)
        is SetTerrainTile -> SET_TERRAIN_TILE(state, action)
        // world
        is DeleteWorld -> deleteElement(state, action.id, State::canDeleteWorld)
    }
}
