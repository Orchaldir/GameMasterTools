package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateWorld
import at.orchaldir.gm.core.action.UpdateWorld
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_WORLD: Reducer<CreateWorld, State> = { state, _ ->
    val moon = World(state.getWorldStorage().nextId)

    noFollowUps(state.updateStorage(state.getWorldStorage().add(moon)))
}

val UPDATE_WORLD: Reducer<UpdateWorld, State> = { state, action ->
    val world = action.world

    state.getWorldStorage().require(world.id)
    validateWorld(state, world)

    noFollowUps(state.updateStorage(state.getWorldStorage().update(world)))
}

fun validateWorld(state: State, moon: World) {

}