package at.orchaldir.gm.core.selector.gm.treasure

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.utils.Id

fun State.canDeleteTreasureParcel(encounter: TreasureParcelId) = DeleteResult(encounter)
    .addElements(getTreasureParcelsWith(encounter))

fun <ID : Id<ID>> State.getTreasureParcelsWith(id: ID) = getTreasureParcelStorage()
    .getAll()
    .filter { it.entry.contains(id) }

