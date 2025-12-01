package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceGroupId
import at.orchaldir.gm.core.model.race.RaceId

fun State.canDeleteRaceGroup(group: RaceGroupId) = DeleteResult(group)

fun State.getRaceGroups(id: RaceId) = getRaceGroupStorage()
    .getAll()
    .filter { it.races.contains(id) }