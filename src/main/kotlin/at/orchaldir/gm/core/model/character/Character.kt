package at.orchaldir.gm.core.model.character

@JvmInline
value class CharacterId(private val id: Int)

data class Character(
    val id: CharacterId,
    val name: String,
    val gender: Gender = Gender.Genderless,
)
