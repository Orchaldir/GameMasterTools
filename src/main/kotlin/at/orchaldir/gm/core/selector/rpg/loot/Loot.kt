package at.orchaldir.gm.core.selector.rpg.loot

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.loot.LootId

fun State.canDeleteLoot(encounter: LootId) = DeleteResult(encounter)
    .addElements(getLootWith(encounter))

fun State.getLootWith(encounter: LootId) = getLootStorage()
    .getAll()
    .filter { it.entry.contains(encounter) }

