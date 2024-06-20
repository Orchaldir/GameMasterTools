package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCharacter(state: State, id: CharacterId, parameters: Parameters): Character {
    val character = state.characters.getOrThrow(id)

    val name = parseCharacterName(parameters)
    val race = RaceId(parameters.getOrFail(RACE).toInt())
    val gender = Gender.valueOf(parameters.getOrFail(GENDER))
    val culture = CultureId(parameters.getOrFail(CULTURE).toInt())
    val personality = parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(GROUP_PREFIX) }
        .map { e -> e.value.first() }
        .filter { it != NONE }
        .map { PersonalityTraitId(it.toInt()) }
        .toSet()

    val origin = when (parameters[ORIGIN]) {
        "Born" -> {
            val father = CharacterId(parameters[FATHER]?.toInt() ?: 0)
            val mother = CharacterId(parameters[MOTHER]?.toInt() ?: 0)
            Born(mother, father)
        }

        else -> UndefinedCharacterOrigin
    }

    return character.copy(
        name = name,
        race = race,
        gender = gender,
        origin = origin,
        culture = culture,
        personality = personality
    )
}

private fun parseCharacterName(parameters: Parameters): CharacterName {
    val given = parameters.getOrFail(GIVEN_NAME)

    return when (parameters.getOrFail(NAME_TYPE)) {
        "FamilyName" -> {
            var middle = parameters[MIDDLE_NAME]

            if (middle.isNullOrEmpty()) {
                middle = null
            }

            FamilyName(
                given,
                middle,
                parameters[FAMILY_NAME] ?: "Unknown"
            )
        }

        "Genonym" -> Genonym(given)
        else -> Mononym(given)
    }
}