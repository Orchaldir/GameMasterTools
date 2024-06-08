package at.orchaldir.gm.core.model.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CharacterOrigin

@Serializable
@SerialName("Born")
data class Born(val mother: CharacterId, val father: CharacterId) : CharacterOrigin() {

    fun isParent(id: CharacterId) = mother == id || father == id
}

@Serializable
@SerialName("Undefined")
data object UndefinedCharacterOrigin : CharacterOrigin()
