package at.orchaldir.gm.core.model.character

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CharacterId(private val id: Int)

@Serializable
data class Character(
    val id: CharacterId,
    val name: String,
    val gender: Gender = Gender.Genderless,
)
