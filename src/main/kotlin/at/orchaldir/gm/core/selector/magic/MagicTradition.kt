package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.magic.SpellGroupId

fun State.canDeleteMagicTradition(tradition: MagicTraditionId) = DeleteResult(tradition)

fun State.countMagicTraditions(group: SpellGroupId) = getMagicTraditionStorage()
    .getAll()
    .count { it.groups.contains(group) }

fun State.getMagicTraditions(group: SpellGroupId) = getMagicTraditionStorage()
    .getAll()
    .filter { it.groups.contains(group) }

