package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CultureId

fun State.getCharacters(culture: CultureId) = characters.getAll().filter { c -> c.culture == culture }