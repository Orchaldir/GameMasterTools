package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.CreateTownMap
import at.orchaldir.gm.core.action.DeleteTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN_MAP: Reducer<CreateTownMap, State> = { state, _ ->
    val town = TownMap(state.getTownMapStorage().nextId)

    noFollowUps(state.updateStorage(state.getTownMapStorage().add(town)))
}

val DELETE_TOWN_MAP: Reducer<DeleteTownMap, State> = { state, action ->
    state.getTownMapStorage().require(action.id)

    // TODO: check town

    noFollowUps(state.updateStorage(state.getTownMapStorage().remove(action.id)))
}

fun validateTownMap(state: State, town: TownMap) {
    town.map.tiles.forEach { validateTownTile(state, it) }
}

private fun validateTownTile(state: State, tile: TownTile) {
    validateConstruction(state, tile.construction)
    validateTerrain(state, tile.terrain)
}

private fun validateConstruction(state: State, construction: Construction) = when (construction) {
    AbstractBuildingTile -> doNothing()
    is BuildingTile -> state.getBuildingStorage().require(construction.building)
    NoConstruction -> doNothing()
    is StreetTile -> {
        state.getStreetTemplateStorage().require(construction.templateId)
        state.getStreetStorage().requireOptional(construction.streetId)
    }
}

private fun validateTerrain(state: State, terrain: Terrain) = when (terrain) {
    is HillTerrain -> state.getMountainStorage().require(terrain.mountain)
    is MountainTerrain -> state.getMountainStorage().require(terrain.mountain)
    PlainTerrain -> doNothing()
    is RiverTerrain -> state.getRiverStorage().require(terrain.river)
}
