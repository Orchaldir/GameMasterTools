package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.NamingConventionType.*
import at.orchaldir.gm.core.model.culture.style.HairStyleType
import at.orchaldir.gm.core.model.culture.style.StyleOptions
import at.orchaldir.gm.core.model.race.appearance.BeardStyleType
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCulture(
    parameters: Parameters,
    id: CultureId,
): Culture {
    val name = parameters.getOrFail(NAME)

    return Culture(
        id,
        name,
        parseNamingConvention(parameters),
        StyleOptions(
            parseRarityMap(parameters, BEARD_STYLE, BeardStyleType::valueOf),
            parseRarityMap(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
            parseRarityMap(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
            parseRarityMap(parameters, HAIR_STYLE, HairStyleType::valueOf),
            parseRarityMap(parameters, LIP_COLORS, Color::valueOf),
        ),
    )
}

fun parseNamingConvention(
    parameters: Parameters,
): NamingConvention {
    return when (parameters[NAMING_CONVENTION]) {
        Mononym.toString() -> MononymConvention(parseNamesByGender(parameters, NAMES))

        Family.toString() -> FamilyConvention(
            parse(parameters, NAME_ORDER, GivenNameFirst),
            parseRarityMap(parameters, MIDDLE_NAME, MiddleNameOption::valueOf, MiddleNameOption.entries),
            parseNamesByGender(parameters, NAMES),
            parseNamesByGender(parameters, FAMILY_NAMES)
        )

        else -> NoNamingConvention
    }
}

fun parseNamesByGender(
    parameters: Parameters,
    param: String,
): GenderMap<NameListId> {
    val female = parseNameLisId(parameters, param, Gender.Female)
    val genderless = parseNameLisId(parameters, param, Gender.Genderless)
    val male = parseNameLisId(parameters, param, Gender.Male)

    return GenderMap(female, genderless, male)
}

private fun parseNameLisId(
    parameters: Parameters,
    param: String,
    gender: Gender,
) = NameListId(parameters["$param-$gender"]?.toInt() ?: 0)