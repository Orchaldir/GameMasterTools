package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateArchitecturalStyle
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ARCHITECTURAL_STYLE: Reducer<CreateArchitecturalStyle, State> = { state, _ ->
    val style = ArchitecturalStyle(state.getArchitecturalStyleStorage().nextId)

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().add(style)))
}

val DELETE_ARCHITECTURAL_STYLE: Reducer<DeleteArchitecturalStyle, State> = { state, action ->
    state.getArchitecturalStyleStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().remove(action.id)))
}

val UPDATE_ARCHITECTURAL_STYLE: Reducer<UpdateArchitecturalStyle, State> = { state, action ->
    state.getArchitecturalStyleStorage().require(action.style.id)

    action.style.revival?.let {
        state.getArchitecturalStyleStorage()
            .require(it) { "Cannot revive unknown architectural style ${it.value}!" }
    }

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().update(action.style)))
}