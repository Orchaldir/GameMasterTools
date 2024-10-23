package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.getAgeInYears(town: Town) = getDefaultCalendar()
    .getDurationInYears(town.foundingDate, time.currentDate)

fun countTowns(buildings: Collection<Building>) = buildings
    .groupingBy { it.lot.town }
    .eachCount()

fun State.exists(id: TownId, date: Date) = exists(getTownStorage().getOrThrow(id), date)

fun State.exists(town: Town, date: Date) = getDefaultCalendar().compareTo(town.foundingDate, date) <= 0

fun State.getTowns(mountain: MountainId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(mountain) } }

fun State.getTowns(river: RiverId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

fun State.getTowns(street: StreetId) = getTownStorage().getAll()
    .filter { it.map.contains { it.construction.contains(street) } }

// map size

fun getMinWidthStart(town: Town) = mapIndexOfConstructions(town, town.map.size.width - 1) { index ->
    -town.map.size.toX(index)
}

fun getMinWidthEnd(town: Town) = mapIndexOfConstructions(town, town.map.size.width - 1) { index ->
    1 - (town.map.size.width - town.map.size.toX(index))
}

fun getMinHeightStart(town: Town) = mapIndexOfConstructions(town, town.map.size.height - 1) { index ->
    -town.map.size.toY(index)
}

fun getMinHeightEnd(town: Town) = mapIndexOfConstructions(town, town.map.size.height - 1) { index ->
    1 - (town.map.size.height - town.map.size.toY(index))
}

private fun mapIndexOfConstructions(town: Town, default: Int, indexLookup: (Int) -> Int) = town.map.tiles
    .withIndex()
    .mapNotNull { (index, tile) ->
        if (!tile.canBuild()) {
            indexLookup(index)
        } else {
            null
        }
    }
    .maxOrNull()
    ?: default