package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateMoon
import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.world.canDeleteMoon
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MOON: Reducer<CreateMoon, State> = { state, _ ->
    val moon = Moon(state.getMoonStorage().nextId)

    noFollowUps(state.updateStorage(state.getMoonStorage().add(moon)))
}

val DELETE_MOON: Reducer<DeleteMoon, State> = { state, action ->
    state.getMoonStorage().require(action.id)

    validateCanDelete(state.canDeleteMoon(action.id), action.id)

    noFollowUps(state.updateStorage(state.getMoonStorage().remove(action.id)))
}

val UPDATE_MOON: Reducer<UpdateMoon, State> = { state, action ->
    val moon = action.moon

    state.getMoonStorage().require(moon.id)
    validateMoon(state, moon)

    noFollowUps(state.updateStorage(state.getMoonStorage().update(moon)))
}

fun validateMoon(state: State, moon: Moon) {
    state.getPlaneStorage().requireOptional(moon.plane)
    require(moon.daysPerQuarter > 0) { "Days per quarter most be greater than 0!" }
}