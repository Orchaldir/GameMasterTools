package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.CharacterOriginType.Undefined
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.selector.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCharacterId(parameters: Parameters, param: String) = CharacterId(parseInt(parameters, param))

fun parseCharacter(
    state: State,
    parameters: Parameters,
    id: CharacterId,
): Character {
    val character = state.characters.getOrThrow(id)

    val name = parseCharacterName(parameters)
    val race = RaceId(parameters.getOrFail(RACE).toInt())
    val gender = Gender.valueOf(parameters.getOrFail(GENDER))
    val culture = CultureId(parameters.getOrFail(CULTURE).toInt())
    val personality = parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(PERSONALITY_PREFIX) }
        .map { e -> e.value.first() }
        .filter { it != NONE }
        .map { PersonalityTraitId(it.toInt()) }
        .toSet()

    val origin = when (parse(parameters, ORIGIN, Undefined)) {
        CharacterOriginType.Born -> {
            val father = parseCharacterId(parameters, FATHER)
            val mother = parseCharacterId(parameters, MOTHER)
            Born(mother, father)
        }

        Undefined -> UndefinedCharacterOrigin
    }
    val birthDate = parseDay(parameters, state.getDefaultCalendar(), combine(ORIGIN, DATE))
    val causeOfDeath = when (parse(parameters, DEATH, CauseOfDeathType.Alive)) {
        CauseOfDeathType.Alive -> Alive
        CauseOfDeathType.Accident -> Accident(parseDeathDay(parameters, state))
        CauseOfDeathType.Murder -> Murder(
            parseDeathDay(parameters, state),
            parseCharacterId(parameters, KILLER),
        )

        CauseOfDeathType.OldAge -> OldAge(parseDeathDay(parameters, state))
    }

    return character.copy(
        name = name,
        race = race,
        gender = gender,
        origin = origin,
        birthDate = birthDate,
        causeOfDeath = causeOfDeath,
        culture = culture,
        personality = personality
    )
}

private fun parseDeathDay(
    parameters: Parameters,
    state: State,
) = parseDay(parameters, state.getDefaultCalendar(), combine(DEATH, DATE))

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