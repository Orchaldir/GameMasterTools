package at.orchaldir.gm.model

import at.orchaldir.gm.model.character.Character
import at.orchaldir.gm.model.character.CharacterId

data class State(val characters: Map<CharacterId, Character> = mapOf())