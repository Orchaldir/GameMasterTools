package at.orchaldir.gm.core.selector.gm.treasure

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId

fun State.canDeleteTreasureParcel(encounter: TreasureParcelId) = DeleteResult(encounter)
    .addElements(getTreasureParcelWith(encounter))

fun State.getTreasureParcelWith(encounter: TreasureParcelId) = getTreasureParcelStorage()
    .getAll()
    .filter { it.entry.contains(encounter) }

