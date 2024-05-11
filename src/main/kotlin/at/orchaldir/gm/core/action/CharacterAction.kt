package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.character.CharacterId

sealed class CharacterAction
data object CreateCharacter : CharacterAction()
data class DeleteCharacter(val id: CharacterId) : CharacterAction()