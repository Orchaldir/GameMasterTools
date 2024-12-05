package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.economy.getOwnedBusinesses
import at.orchaldir.gm.core.selector.economy.getPreviouslyOwnedBusinesses
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.utils.Id

fun State.canDelete(town: TownId) = getBuildings(town).isEmpty()
        && getOwnedBuildings(town).isEmpty()
        && getPreviouslyOwnedBuildings(town).isEmpty()
        && getOwnedBusinesses(town).isEmpty()
        && getPreviouslyOwnedBusinesses(town).isEmpty()

// get

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

fun State.getTowns(type: StreetTypeId) = getTownStorage().getAll()
    .filter { it.map.contains { it.construction.contains(type) } }

// founder

fun <ID : Id<ID>> State.getTownsFoundedBy(id: ID) = getTownStorage().getAll()
    .filter { it.founder.wasCreatedBy(id) }

// map size

fun getMinWidthStart(town: Town) = mapIndexOfConstructions(town, 1 - town.map.size.width) { index ->
    -town.map.size.toX(index)
}

fun getMinWidthEnd(town: Town) = mapIndexOfConstructions(town, 1 - town.map.size.width) { index ->
    1 - (town.map.size.width - town.map.size.toX(index))
}

fun getMinHeightStart(town: Town) = mapIndexOfConstructions(town, 1 - town.map.size.height) { index ->
    -town.map.size.toY(index)
}

fun getMinHeightEnd(town: Town) = mapIndexOfConstructions(town, 1 - town.map.size.height) { index ->
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