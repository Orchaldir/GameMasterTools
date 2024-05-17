package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CharacterId(val value: Int) : Id<CharacterId> {

    override fun next() = CharacterId(value + 1)

}

@Serializable
data class Character(
    val id: CharacterId,
    val name: String = "Character ${id.value}",
    val race: RaceId = RaceId(0),
    val gender: Gender = Gender.Genderless,
    val culture: CultureId? = null,
) : Element<CharacterId> {

    override fun id() = id
    override fun name() = name

}