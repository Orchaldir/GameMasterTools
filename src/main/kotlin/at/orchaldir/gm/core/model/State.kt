package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.utils.Storage

data class State(val characters: Storage<CharacterId, Character>)