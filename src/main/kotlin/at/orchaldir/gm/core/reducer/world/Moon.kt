package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.moon.ALLOWED_MOON_POSITIONS
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MOON: Reducer<CreateMoon, State> = { state, _ ->
    val moon = Moon(state.getMoonStorage().nextId)

    noFollowUps(state.updateStorage(state.getMoonStorage().add(moon)))
}

val UPDATE_MOON: Reducer<UpdateMoon, State> = { state, action ->
    val moon = action.moon

    state.getMoonStorage().require(moon.id)
    validateMoon(state, moon)

    noFollowUps(state.updateStorage(state.getMoonStorage().update(moon)))
}

fun validateMoon(state: State, moon: Moon) {
    state.getPlaneStorage().requireOptional(moon.plane)
    checkPosition(state, moon.position, "position", null, ALLOWED_MOON_POSITIONS)
    require(moon.daysPerQuarter > 0) { "Days per quarter most be greater than 0!" }
}