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
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyleType
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
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
        parseSomeOf(parameters, LANGUAGES, ::parseLanguageId),
        parseNamingConvention(parameters),
        AppearanceStyle(
            parseOneOf(parameters, BEARD_STYLE, BeardStyleType::valueOf),
            parseOneOf(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
            parseOneOf(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
            parseOneOf(parameters, HAIR_STYLE, HairStyleType::valueOf),
            parseOneOf(parameters, LIP_COLORS, Color::valueOf),
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
            parseOneOf(parameters, MIDDLE_NAME, MiddleNameOption::valueOf, MiddleNameOption.entries),
            parseNamesByGender(parameters, NAMES),
            parseNameListId(parameters, FAMILY_NAMES)
        )

        Patronym.toString() -> PatronymConvention(
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
            parseNamesByGender(parameters, NAMES),
        )

        Matronym.toString() -> MatronymConvention(
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
            parseNamesByGender(parameters, NAMES),
        )

        Genonym.toString() -> GenonymConvention(
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
            parseNamesByGender(parameters, NAMES),
        )

        else -> NoNamingConvention
    }
}

fun parseGenonymicStyle(
    parameters: Parameters,
): GenonymicStyle {
    return when (parameters[GENONYMIC_STYLE]) {
        GenonymicStyleType.ChildOf.toString() -> ChildOfStyle(parseWordsByGender(parameters, WORD))
        GenonymicStyleType.Prefix.toString() -> PrefixStyle(parseWordsByGender(parameters, WORD))
        GenonymicStyleType.Suffix.toString() -> SuffixStyle(parseWordsByGender(parameters, WORD))

        else -> NamesOnlyStyle
    }
}

fun parseNamesByGender(
    parameters: Parameters,
    param: String,
): GenderMap<NameListId> {
    val female = parseNameListId(parameters, param, Gender.Female)
    val genderless = parseNameListId(parameters, param, Gender.Genderless)
    val male = parseNameListId(parameters, param, Gender.Male)

    return GenderMap(female, genderless, male)
}

private fun parseNameListId(
    parameters: Parameters,
    param: String,
    gender: Gender,
) = parseNameListId(parameters, "$param-$gender")

private fun parseNameListId(
    parameters: Parameters,
    param: String,
) = NameListId(parameters[param]?.toInt() ?: 0)

fun parseWordsByGender(
    parameters: Parameters,
    param: String,
): GenderMap<String> {
    val female = parseWord(parameters, param, Gender.Female)
    val genderless = parseWord(parameters, param, Gender.Genderless)
    val male = parseWord(parameters, param, Gender.Male)

    return GenderMap(female, genderless, male)
}

private fun parseWord(
    parameters: Parameters,
    param: String,
    gender: Gender,
) = parameters["$param-$gender"] ?: "Unknown"