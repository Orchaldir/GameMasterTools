package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.getAgeInYears(building: Building) = getDefaultCalendar()
    .getDurationInYears(building.constructionDate, time.currentDate)

fun State.canDelete(building: BuildingId) = true

fun State.getBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.lot.town == town }

