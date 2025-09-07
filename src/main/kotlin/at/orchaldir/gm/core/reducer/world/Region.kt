package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateRegion
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDeleteRegion
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MOUNTAIN: Reducer<CreateRegion, State> = { state, _ ->
    val moon = Region(state.getRegionStorage().nextId)

    noFollowUps(state.updateStorage(state.getRegionStorage().add(moon)))
}

val UPDATE_MOUNTAIN: Reducer<UpdateRegion, State> = { state, action ->
    val region = action.region
    state.getRegionStorage().require(region.id)

    validateRegion(state, region)

    noFollowUps(state.updateStorage(state.getRegionStorage().update(region)))
}

fun validateRegion(
    state: State,
    region: Region,
) {
    validateRegion(state, region.data)
    checkPosition(state, region.position, "position", null, region.data.getAllowedRegionTypes())
    region.resources.forEach { state.getMaterialStorage().require(it) }
}

fun validateRegion(
    state: State,
    data: RegionData,
) = when (data) {
    is Battlefield -> state.getBattleStorage().requireOptional(data.battle)
    Continent, Desert, Forrest, Lake, Mountain, Sea, UndefinedRegionData -> doNothing()
    is Wasteland -> state.getCatastropheStorage().requireOptional(data.catastrophe)
}