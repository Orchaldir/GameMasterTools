package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun validateStartDate(state: State, style: ArchitecturalStyle, building: BuildingId, constructionDate: Date?) =
    validateStartDate(state.getDefaultCalendar(), style, building, constructionDate)

fun validateStartDate(calendar: Calendar, style: ArchitecturalStyle, building: BuildingId, constructionDate: Date?) {
    require(calendar.isAfterOrEqualOptional(constructionDate, style.start)) {
        "Architectural Style ${style.id.value} didn't exist yet, when building ${building.value} was build!"
    }
}