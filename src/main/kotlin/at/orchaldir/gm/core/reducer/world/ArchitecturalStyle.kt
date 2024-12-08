package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateArchitecturalStyle
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ARCHITECTURAL_STYLE: Reducer<CreateArchitecturalStyle, State> = { state, _ ->
    val style = ArchitecturalStyle(state.getArchitecturalStyleStorage().nextId)

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().add(style)))
}

val DELETE_ARCHITECTURAL_STYLE: Reducer<DeleteArchitecturalStyle, State> = { state, action ->
    state.getArchitecturalStyleStorage().require(action.id)

    require(state.canDelete(action.id)) { "Architectural Style ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().remove(action.id)))
}

val UPDATE_ARCHITECTURAL_STYLE: Reducer<UpdateArchitecturalStyle, State> = { state, action ->
    state.getArchitecturalStyleStorage().require(action.style.id)

    state.getBuildings(action.style.id).forEach { checkStartDate(state, action.style, it.id, it.constructionDate) }

    action.style.revival?.let {
        state.getArchitecturalStyleStorage()
            .require(it) { "Cannot revive unknown architectural style ${it.value}!" }
    }

    action.style.end?.let { end ->
        require(action.style.start < end) { "Architectural style must end after it started!" }
    }

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().update(action.style)))
}

fun checkStartDate(state: State, style: ArchitecturalStyle, building: BuildingId, constructionDate: Date?) {
    if (constructionDate != null) {
        val calendar = state.getDefaultCalendar()
        val compareTo = calendar.compareTo(style.start, constructionDate)

        require(compareTo <= 0) { "Architectural Style ${style.id.value} didn't exist yet, when building ${building.value} was build!" }
    }
}