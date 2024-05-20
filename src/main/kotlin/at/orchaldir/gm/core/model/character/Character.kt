package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CharacterId(val value: Int) : Id<CharacterId> {

    override fun next() = CharacterId(value + 1)
    override fun value() = value

}

@Serializable
data class Character(
    val id: CharacterId,
    val name: String = "Character ${id.value}",
    val race: RaceId = RaceId(0),
    val gender: Gender = Gender.Genderless,
    val origin: CharacterOrigin = UndefinedCharacterOrigin,
    val culture: CultureId? = null,
    val personality: Set<PersonalityTraitId> = setOf(),
    val languages: Map<LanguageId, ComprehensionLevel> = mapOf(LanguageId(0) to ComprehensionLevel.Native),
) : Element<CharacterId> {

    override fun id() = id
    override fun name() = name

}