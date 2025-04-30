package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.TitleId

fun State.canDeleteTitle(id: TitleId) = countCharacters(id) == 0
