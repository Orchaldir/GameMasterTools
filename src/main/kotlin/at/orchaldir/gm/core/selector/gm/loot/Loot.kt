package at.orchaldir.gm.core.selector.gm.loot

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.loot.LootId

fun State.canDeleteLoot(encounter: LootId) = DeleteResult(encounter)
    .addElements(getLootWith(encounter))

fun State.getLootWith(encounter: LootId) = getLootStorage()
    .getAll()
    .filter { it.entry.contains(encounter) }

