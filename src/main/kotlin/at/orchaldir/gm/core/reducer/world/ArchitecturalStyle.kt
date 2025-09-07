package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.CreateArchitecturalStyle
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ARCHITECTURAL_STYLE: Reducer<CreateArchitecturalStyle, State> = { state, _ ->
    val style = ArchitecturalStyle(state.getArchitecturalStyleStorage().nextId)

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().add(style)))
}

val UPDATE_ARCHITECTURAL_STYLE: Reducer<UpdateArchitecturalStyle, State> = { state, action ->
    val style = action.style
    state.getArchitecturalStyleStorage().require(style.id)
    validateArchitecturalStyle(state, style)

    noFollowUps(state.updateStorage(state.getArchitecturalStyleStorage().update(style)))
}

fun validateArchitecturalStyle(
    state: State,
    style: ArchitecturalStyle,
) {
    val calendar = state.getDefaultCalendar()

    checkDate(state, style.start, "Architectural Style's Start")
    checkDate(state, style.end, "Architectural Style's End")
    state.getBuildings(style.id).forEach { checkStartDate(calendar, style, it.id, it.constructionDate) }

    style.revival?.let {
        state.getArchitecturalStyleStorage()
            .require(it) { "Cannot revive unknown architectural style ${it.value}!" }
    }

    validateHasStartAndEnd(state, style)
}

fun checkStartDate(state: State, style: ArchitecturalStyle, building: BuildingId, constructionDate: Date?) =
    checkStartDate(state.getDefaultCalendar(), style, building, constructionDate)

fun checkStartDate(calendar: Calendar, style: ArchitecturalStyle, building: BuildingId, constructionDate: Date?) {
    require(calendar.isAfterOrEqualOptional(constructionDate, style.start)) {
        "Architectural Style ${style.id.value} didn't exist yet, when building ${building.value} was build!"
    }
}