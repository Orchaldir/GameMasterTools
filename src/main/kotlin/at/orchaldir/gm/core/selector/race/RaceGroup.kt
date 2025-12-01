package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceGroupId

fun State.canDeleteRaceGroup(group: RaceGroupId) = DeleteResult(group)