package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.RaceId

fun State.canDelete(race: RaceId) = getCharacters(race).isEmpty()