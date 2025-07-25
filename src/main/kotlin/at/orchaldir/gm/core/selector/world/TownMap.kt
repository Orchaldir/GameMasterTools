package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.util.getStartDateComparator

fun State.canDeleteTownMap(town: TownMapId) = getBuildings(town).isEmpty()

// count

fun countEachTown(buildings: Collection<Building>) = buildings
    .groupingBy { it.lot.town }
    .eachCount()

// get

fun State.getCurrentTownMap(town: TownId): TownMap? {
    return getTownMaps(town)
        .maxWithOrNull(getStartDateComparator())
}

fun State.getTownMaps(town: TownId) = getTownMapStorage()
    .getAll()
    .filter { it.town == town }

fun State.getTowns(mountain: RegionId) = getTownMapStorage()
    .getAll()
    .filter { it.map.contains { it.terrain.contains(mountain) } }

fun State.getTowns(river: RiverId) = getTownMapStorage()
    .getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

fun State.getTowns(street: StreetId) = getTownMapStorage()
    .getAll()
    .filter { it.map.contains { it.construction.contains(street) } }

fun State.getTowns(type: StreetTemplateId) = getTownMapStorage()
    .getAll()
    .filter { it.map.contains { it.construction.contains(type) } }


// map size

fun getMinWidthStart(townMap: TownMap) = mapIndexOfConstructions(townMap, 1 - townMap.map.size.width) { index ->
    -townMap.map.size.toX(index)
}

fun getMinWidthEnd(townMap: TownMap) = mapIndexOfConstructions(townMap, 1 - townMap.map.size.width) { index ->
    1 - (townMap.map.size.width - townMap.map.size.toX(index))
}

fun getMinHeightStart(townMap: TownMap) = mapIndexOfConstructions(townMap, 1 - townMap.map.size.height) { index ->
    -townMap.map.size.toY(index)
}

fun getMinHeightEnd(townMap: TownMap) = mapIndexOfConstructions(townMap, 1 - townMap.map.size.height) { index ->
    1 - (townMap.map.size.height - townMap.map.size.toY(index))
}

private fun mapIndexOfConstructions(townMap: TownMap, default: Int, indexLookup: (Int) -> Int) = townMap.map.tiles
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