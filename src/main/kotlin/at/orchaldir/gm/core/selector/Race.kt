package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId

fun State.canDelete(race: RaceId) = getRaceStorage().getSize() > 1 && getCharacters(race).isEmpty()

fun State.getRaces(id: RaceAppearanceId) = getRaceStorage().getAll()
    .filter { it.lifeStages.contains(id) }