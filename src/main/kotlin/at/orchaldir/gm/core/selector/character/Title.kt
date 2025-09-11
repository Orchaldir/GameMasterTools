package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.TitleId

fun State.canDeleteTitle(id: TitleId) = DeleteResult(id)
    .addElements(getCharacters(id))
