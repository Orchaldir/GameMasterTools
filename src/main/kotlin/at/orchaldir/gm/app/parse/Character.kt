package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.CharacterOriginType.Undefined
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentYear
import io.ktor.http.*
import io.ktor.server.util.*
import kotlin.random.Random

fun parseCharacterId(parameters: Parameters, param: String) = CharacterId(parseInt(parameters, param))
fun parseCharacterId(value: String) = CharacterId(value.toInt())
fun parseOptionalCharacterId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { CharacterId(it) }

fun parseCharacter(
    state: State,
    parameters: Parameters,
    id: CharacterId,
): Character {
    val character = state.getCharacterStorage().getOrThrow(id)

    val name = parseCharacterName(parameters)
    val race = RaceId(parameters.getOrFail(RACE).toInt())
    val culture = CultureId(parameters.getOrFail(CULTURE).toInt())
    val origin = when (parse(parameters, ORIGIN, Undefined)) {
        CharacterOriginType.Born -> {
            val father = parseCharacterId(parameters, FATHER)
            val mother = parseCharacterId(parameters, MOTHER)
            Born(mother, father)
        }

        Undefined -> UndefinedCharacterOrigin
    }
    val birthDate = parseBirthday(parameters, state, race)

    return character.copy(
        name = name,
        race = race,
        gender = parseGender(parameters),
        origin = origin,
        birthDate = birthDate,
        vitalStatus = parseVitalStatus(parameters, state),
        culture = culture,
        personality = parsePersonality(parameters),
        housingStatus = parseHousingStatusHistory(parameters, state, birthDate),
        employmentStatus = parseEmploymentStatusHistory(parameters, state, birthDate),
        beliefStatus = parseBeliefStatusHistory(parameters, state, birthDate),
    )
}

fun parseGender(parameters: Parameters) = Gender.valueOf(parameters.getOrFail(GENDER))

private fun parseBirthday(
    parameters: Parameters,
    state: State,
    raceId: RaceId,
): Date {
    val index = parameters[LIFE_STAGE]?.toIntOrNull()

    if (index != null) {
        val race = state.getRaceStorage().getOrThrow(raceId)
        val minAge = if (index > 0) {
            race.lifeStages.getAllLifeStages()[index - 1].maxAge
        } else {
            0
        }
        val maxAge = race.lifeStages.getAllLifeStages()[index].maxAge
        val age = Random.nextInt(minAge, maxAge)

        return Year(state.getCurrentYear().year - age)
    }

    return parseDate(parameters, state.getDefaultCalendar(), combine(ORIGIN, DATE))
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