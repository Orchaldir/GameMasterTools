package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.world.town.*
import at.orchaldir.gm.utils.redux.Reducer

val WORLD_REDUCER: Reducer<WorldAction, State> = { state, action ->
    when (action) {
        // architectural style
        is CreateArchitecturalStyle -> CREATE_ARCHITECTURAL_STYLE(state, action)
        is DeleteArchitecturalStyle -> DELETE_ARCHITECTURAL_STYLE(state, action)
        is UpdateArchitecturalStyle -> UPDATE_ARCHITECTURAL_STYLE(state, action)
        // moon
        is CreateMoon -> CREATE_MOON(state, action)
        is DeleteMoon -> DELETE_MOON(state, action)
        is UpdateMoon -> UPDATE_MOON(state, action)
        // plane
        is CreatePlane -> CREATE_PLANE(state, action)
        is DeletePlane -> DELETE_PLANE(state, action)
        is UpdatePlane -> UPDATE_PLANE(state, action)
        // region
        is CreateRegion -> CREATE_MOUNTAIN(state, action)
        is DeleteRegion -> DELETE_MOUNTAIN(state, action)
        is UpdateRegion -> UPDATE_MOUNTAIN(state, action)
        // river
        is CreateRiver -> CREATE_RIVER(state, action)
        is DeleteRiver -> DELETE_RIVER(state, action)
        is UpdateRiver -> UPDATE_RIVER(state, action)
        // street
        is CreateStreet -> CREATE_STREET(state, action)
        is DeleteStreet -> DELETE_STREET(state, action)
        is UpdateStreet -> UPDATE_STREET(state, action)
        // street template
        is CreateStreetTemplate -> CREATE_STREET_TEMPLATE(state, action)
        is DeleteStreetTemplate -> DELETE_STREET_TEMPLATE(state, action)
        is UpdateStreetTemplate -> UPDATE_STREET_TEMPLATE(state, action)
        // town
        is CreateTownMap -> CREATE_TOWN_MAP(state, action)
        is DeleteTownMap -> DELETE_TOWN_MAP(state, action)
        is UpdateTownMap -> UPDATE_TOWN_MAP(state, action)
        // town's abstract buildings
        is AddAbstractBuilding -> ADD_ABSTRACT_BUILDING(state, action)
        is RemoveAbstractBuilding -> REMOVE_ABSTRACT_BUILDING(state, action)
        // town's buildings
        is AddBuilding -> ADD_BUILDING(state, action)
        is DeleteBuilding -> DELETE_BUILDING(state, action)
        is UpdateBuilding -> UPDATE_BUILDING(state, action)
        is UpdateBuildingLot -> UPDATE_BUILDING_LOT(state, action)
        // town's streets
        is AddStreetTile -> ADD_STREET_TILE(state, action)
        is RemoveStreetTile -> REMOVE_STREET_TILE(state, action)
        // town's terrain
        is ResizeTerrain -> RESIZE_TERRAIN(state, action)
        is SetTerrainTile -> SET_TERRAIN_TILE(state, action)
    }
}
