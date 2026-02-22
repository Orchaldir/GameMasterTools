package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.model.world.terrain.RiverId

fun State.canDeleteRiver(river: RiverId) = DeleteResult(river)
    .addElements(getSettlementMaps(river))

fun State.getRivers(settlement: SettlementMapId) = getRiverStorage().get(getRiverIds(settlement))

fun State.getRiverIds(settlement: SettlementMapId) = getSettlementMapStorage()
    .getOrThrow(settlement)
    .map.tiles.mapNotNull { it.terrain.getRiver() }
    .distinct()


