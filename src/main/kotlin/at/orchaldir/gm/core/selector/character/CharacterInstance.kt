package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterInstanceId

fun State.canDeleteCharacterInstance(instance: CharacterInstanceId) = DeleteResult(instance)
