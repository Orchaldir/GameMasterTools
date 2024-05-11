package at.orchaldir.gm.core.action

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender

sealed class CharacterAction
data object CreateCharacter : CharacterAction()
data class DeleteCharacter(val id: CharacterId) : CharacterAction()
data class UpdateCharacter(
    val id: CharacterId,
    val name: String,
    val gender: Gender,
) : CharacterAction()