package at.orchaldir.gm.model.character

@JvmInline
value class CharacterId(private val id: Int)

data class Character(
    val id: CharacterId,
    val name: String,
    val gender: Gender,
)
