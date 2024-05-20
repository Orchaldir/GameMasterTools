package at.orchaldir.gm.core.model.character

import kotlinx.serialization.Serializable

@Serializable
sealed class CharacterOrigin
data class Born(val mother: CharacterId, val father: CharacterId) : CharacterOrigin() {

    fun isParent(id: CharacterId) = mother == id || father == id
}

data object UndefinedCharacterOrigin : CharacterOrigin()
