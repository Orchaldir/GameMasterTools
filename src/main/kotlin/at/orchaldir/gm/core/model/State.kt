package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Culture
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.utils.Storage

data class State(
    val characters: Storage<CharacterId, Character>,
    val cultures: Storage<CultureId, Culture>,
)