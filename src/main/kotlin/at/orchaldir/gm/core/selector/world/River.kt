package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId

fun State.canDeleteRiver(river: RiverId) = DeleteResult(river)
    .addElements(getSettlementMaps(river))

fun State.getRivers(town: SettlementMapId) = getRiverStorage().get(getRiverIds(town))

fun State.getRiverIds(town: SettlementMapId) = getSettlementMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getRiver() }
    .distinct()


