package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId

data class State(val characters: Map<CharacterId, Character> = mapOf())