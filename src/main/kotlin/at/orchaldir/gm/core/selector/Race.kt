package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceId

fun State.canDelete(race: RaceId) = races.getSize() > 1 && getCharacters(race).isEmpty()