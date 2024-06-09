package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CultureId

fun State.canDelete(culture: CultureId) = getCharacters(culture).isEmpty()