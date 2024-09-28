package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.getAgeInYears(town: Town) = getDefaultCalendar()
    .getDurationInYears(town.foundingDate, time.currentDate)

fun State.exists(id: TownId, date: Date) = exists(getTownStorage().getOrThrow(id), date)

fun State.exists(town: Town, date: Date) = getDefaultCalendar().compareTo(town.foundingDate, date) <= 0

fun State.getTowns(mountain: MountainId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(mountain) } }

fun State.getTowns(river: RiverId) = getTownStorage().getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

fun State.getTowns(street: StreetId) = getTownStorage().getAll()
    .filter { it.map.contains { it.construction.contains(street) } }

// map size

fun getMinWidthStart(town: Town) = town.map.tiles
    .withIndex()
    .mapNotNull { (index, tile) ->
        if (!tile.canBuild()) {
            -town.map.size.toX(index)
        } else {
            null
        }
    }
    .maxOrNull()
    ?: (town.map.size.width - 1)

fun getMinWidthEnd(town: Town) = town.map.tiles
    .withIndex()
    .mapNotNull { (index, tile) ->
        if (!tile.canBuild()) {
            1 - (town.map.size.width - town.map.size.toX(index))
        } else {
            null
        }
    }
    .maxOrNull()
    ?: (town.map.size.width - 1)

fun getMinHeightStart(town: Town) = town.map.tiles
    .withIndex()
    .mapNotNull { (index, tile) ->
        if (!tile.canBuild()) {
            -town.map.size.toY(index)
        } else {
            null
        }
    }
    .maxOrNull()
    ?: (town.map.size.height - 1)

fun getMinHeightEnd(town: Town) = town.map.tiles
    .withIndex()
    .mapNotNull { (index, tile) ->
        if (!tile.canBuild()) {
            1 - (town.map.size.height - town.map.size.toY(index))
        } else {
            null
        }
    }
    .maxOrNull()
    ?: (town.map.size.height - 1)