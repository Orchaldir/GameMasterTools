package at.orchaldir.gm.core.model.character

import kotlinx.serialization.Serializable

@Serializable
sealed class CharacterOrigin
data class Born(val father: CharacterId?, val mother: CharacterId?) : CharacterOrigin()
data object UndefinedOrigin : CharacterOrigin()
