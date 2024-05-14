package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.model.character.RaceId

fun State.getCharacters(culture: CultureId) = characters.getAll().filter { c -> c.culture == culture }

fun State.getCharacters(race: RaceId) = characters.getAll().filter { c -> c.race == race }