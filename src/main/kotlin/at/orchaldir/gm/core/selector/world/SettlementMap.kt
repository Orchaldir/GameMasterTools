package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.selector.util.canDeleteWithPositions
import at.orchaldir.gm.core.selector.util.getStartDateComparator

fun State.canDeleteSettlementMap(map: SettlementMapId) = DeleteResult(map)
    .apply { canDeleteWithPositions(map, it) }

// get

fun State.getCurrentSettlementMap(settlement: SettlementId): SettlementMap? {
    return getSettlementMaps(settlement)
        .maxWithOrNull(getStartDateComparator())
}

fun State.getSettlementMaps(settlement: SettlementId) = getSettlementMapStorage()
    .getAll()
    .filter { it.settlement == settlement }

fun State.getSettlementMaps(regionId: RegionId) = getSettlementMapStorage()
    .getAll()
    .filter { it.map.contains { it.terrain.contains(regionId) } }

fun State.getSettlementMaps(river: RiverId) = getSettlementMapStorage()
    .getAll()
    .filter { it.map.contains { it.terrain.contains(river) } }

fun State.getSettlementMaps(street: StreetId) = getSettlementMapStorage()
    .getAll()
    .filter { it.map.contains { it.construction.contains(street) } }

fun State.getSettlementMaps(type: StreetTemplateId) = getSettlementMapStorage()
    .getAll()
    .filter { it.map.contains { it.construction.contains(type) } }


// map size

fun getMinWidthStart(map: SettlementMap) = mapIndexOfConstructions(map, 1 - map.map.size.width) { index ->
    -map.map.size.toX(index)
}

fun getMinWidthEnd(map: SettlementMap) = mapIndexOfConstructions(map, 1 - map.map.size.width) { index ->
    1 - (map.map.size.width - map.map.size.toX(index))
}

fun getMinHeightStart(map: SettlementMap) = mapIndexOfConstructions(map, 1 - map.map.size.height) { index ->
    -map.map.size.toY(index)
}

fun getMinHeightEnd(map: SettlementMap) = mapIndexOfConstructions(map, 1 - map.map.size.height) { index ->
    1 - (map.map.size.height - map.map.size.toY(index))
}

private fun mapIndexOfConstructions(map: SettlementMap, default: Int, indexLookup: (Int) -> Int) = map.map.tiles
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