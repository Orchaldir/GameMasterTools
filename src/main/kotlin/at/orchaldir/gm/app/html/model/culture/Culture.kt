package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.time.editHolidays
import at.orchaldir.gm.app.html.model.time.parseCalendarId
import at.orchaldir.gm.app.html.model.time.parseHolidays
import at.orchaldir.gm.app.html.model.time.showHolidays
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.NamingConventionType.*
import at.orchaldir.gm.core.model.culture.style.AppearanceStyle
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.*

// show

fun BODY.showCulture(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    fieldLink("Calendar", call, state, culture.calendar)
    showRarityMap("Languages", culture.languages) { l ->
        link(call, state, l)
    }
    showHolidays(call, state, culture.holidays)
    showNamingConvention(culture.namingConvention, call, state)
    showAppearanceOptions(culture)
    showClothingOptions(call, state, culture)

    h2 { +"Usage" }

    showList("Characters", state.getCharacters(culture.id)) { character ->
        link(call, state, character)
    }
}

private fun BODY.showNamingConvention(
    namingConvention: NamingConvention,
    call: ApplicationCall,
    state: State,
) {
    h2 { +"Naming Convention" }
    field("Type", namingConvention.javaClass.simpleName)
    when (namingConvention) {
        is FamilyConvention -> {
            field("Name Order", namingConvention.nameOrder)
            showRarityMap("Middle Name Options", namingConvention.middleNameOptions)
            showNamesByGender(call, state, "Given Names", namingConvention.givenNames)
            fieldLink("Family Names", call, state, namingConvention.familyNames)
        }

        is GenonymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MatronymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MononymConvention -> showNamesByGender(call, state, "Names", namingConvention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
    }
}

private fun BODY.showGenonymConvention(
    call: ApplicationCall,
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GenderMap<NameListId>,
) {
    field("Lookup Distance", lookupDistance)
    field("Genonymic Style", style.javaClass.simpleName)
    when (style) {
        is ChildOfStyle -> showStyleByGender("Words", style.words)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> showStyleByGender("Prefix", style.prefix)
        is SuffixStyle -> showStyleByGender("Suffix", style.suffix)
    }
    showNamesByGender(call, state, "Names", names)
}

private fun BODY.showNamesByGender(
    call: ApplicationCall,
    state: State,
    label: String,
    namesByGender: GenderMap<NameListId>,
) {
    showDetails(label) {
        showGenderMap(namesByGender) { gender, id ->
            fieldLink(gender.toString(), call, state, id)
        }
    }
}

private fun BODY.showStyleByGender(
    label: String,
    namesByGender: GenderMap<String>,
) {
    showDetails(label) {
        showGenderMap(namesByGender) { gender, text ->
            field(gender.toString(), text)
        }
    }
}

private fun BODY.showAppearanceOptions(culture: Culture) {
    val appearanceStyle = culture.appearanceStyle

    h2 { +"Appearance Options" }
    showRarityMap("Beard Styles", appearanceStyle.beardStyles)
    showRarityMap("Goatee Styles", appearanceStyle.goateeStyles)
    showRarityMap("Moustache Styles", appearanceStyle.moustacheStyles)
    showRarityMap("Hair Styles", appearanceStyle.hairStyles)
    showRarityMap("Short Hair Styles", appearanceStyle.shortHairStyles)
    showRarityMap("Long Hair Styles", appearanceStyle.longHairStyles)
    showRarityMap("Hair Lengths", appearanceStyle.hairLengths)
    showRarityMap("Lip Colors", appearanceStyle.lipColors)
}

private fun BODY.showClothingOptions(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }
    showGenderMap(culture.clothingStyles) { gender, id ->
        optionalFieldLink(gender.toString(), call, state, id)
    }
}

// edit

fun FORM.editCulture(
    state: State,
    culture: Culture,
) {
    selectName(culture.name)
    selectElement(state, "Calendar", CALENDAR_TYPE, state.getCalendarStorage().getAll(), culture.calendar)
    selectRarityMap("Languages", LANGUAGES, state.getLanguageStorage(), culture.languages) { it.name }
    editHolidays(state, culture.holidays)
    editNamingConvention(culture.namingConvention, state)
    editAppearanceOptions(culture)
    editClothingOptions(state, culture)
}

private fun FORM.editNamingConvention(
    namingConvention: NamingConvention,
    state: State,
) {
    h2 { +"Naming Convention" }
    selectValue("Type", NAMING_CONVENTION, NamingConventionType.entries, namingConvention.getType(), true)

    when (namingConvention) {
        is FamilyConvention -> {
            selectValue("Name Order", combine(NAME, ORDER), NameOrder.entries, namingConvention.nameOrder, true)
            selectRarityMap("Middle Name Options", MIDDLE_NAME, namingConvention.middleNameOptions)
            selectNamesByGender(state, "Given Names", namingConvention.givenNames, NAMES)
            field("Family Names") {
                selectNameList(FAMILY_NAMES, state, namingConvention.familyNames)
            }
        }

        is GenonymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MatronymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )

        is MononymConvention -> selectNamesByGender(state, "Names", namingConvention.names, NAMES)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
    }
}

private fun FORM.selectGenonymConvention(
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GenderMap<NameListId>,
) {
    selectValue("Lookup Distance", LOOKUP_DISTANCE, GenonymicLookupDistance.entries, lookupDistance)
    selectValue("Genonymic Style", GENONYMIC_STYLE, GenonymicStyleType.entries, style.getType(), true)

    when (style) {
        is ChildOfStyle -> selectWordsByGender("Words", style.words, WORD)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> selectWordsByGender("Prefix", style.prefix, WORD)
        is SuffixStyle -> selectWordsByGender("Suffix", style.suffix, WORD)
    }
    selectNamesByGender(state, "Names", names, NAMES)
}

private fun FORM.selectNamesByGender(
    state: State,
    fieldLabel: String,
    namesByGender: GenderMap<NameListId>,
    param: String,
) {
    selectGenderMap(fieldLabel, namesByGender) { gender, nameListId ->
        val selectId = "$param-$gender"
        selectNameList(selectId, state, nameListId)
    }
}

private fun HtmlBlockTag.selectNameList(
    selectId: String,
    state: State,
    nameListId: NameListId,
) {
    select {
        id = selectId
        name = selectId
        state.getNameListStorage().getAll().forEach { nameList ->
            option {
                label = nameList.name
                value = nameList.id.value.toString()
                selected = nameList.id == nameListId
            }
        }
    }
}

private fun FORM.selectWordsByGender(label: String, genderMap: GenderMap<String>, param: String) {
    selectGenderMap(label, genderMap) { gender, word ->
        textInput(name = "$param-$gender") {
            value = word
        }
    }
}

private fun FORM.editAppearanceOptions(culture: Culture) {
    h2 { +"Appearance Options" }

    val appearanceStyle = culture.appearanceStyle

    selectRarityMap("Beard Styles", combine(BEARD, STYLE), appearanceStyle.beardStyles)
    selectRarityMap("Goatee Styles", GOATEE_STYLE, appearanceStyle.goateeStyles)
    selectRarityMap("Moustache Styles", MOUSTACHE_STYLE, appearanceStyle.moustacheStyles)
    selectRarityMap("Hair Styles", combine(HAIR, STYLE), appearanceStyle.hairStyles)
    selectRarityMap("Short Hair Styles", combine(SHORT, HAIR, STYLE), appearanceStyle.shortHairStyles)
    selectRarityMap("Long Hair Styles", combine(LONG, HAIR, STYLE), appearanceStyle.longHairStyles)
    selectRarityMap("Hair Lengths", combine(HAIR, LENGTH), appearanceStyle.hairLengths)
    selectRarityMap("Lip Colors", LIP_COLORS, appearanceStyle.lipColors)
}

private fun FORM.editClothingOptions(
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }

    showMap(culture.clothingStyles.getMap()) { gender, fashionId ->
        field(gender.toString()) {
            val selectId = "$FASHION-$gender"
            select {
                id = selectId
                name = selectId
                option {
                    label = "None"
                    value = ""
                    selected = fashionId == null
                }
                state.getFashionStorage().getAll().forEach { fashion ->
                    option {
                        label = fashion.name
                        value = fashion.id.value.toString()
                        selected = fashion.id == fashionId
                    }
                }
            }
        }
    }
}

// parse

fun parseCulture(
    parameters: Parameters,
    id: CultureId,
): Culture {
    val name = parameters.getOrFail(NAME)

    return Culture(
        id,
        name,
        parseCalendarId(parameters, CALENDAR_TYPE),
        parseSomeOf(parameters, LANGUAGES, ::parseLanguageId),
        parseNamingConvention(parameters),
        AppearanceStyle(
            parseOneOf(parameters, combine(BEARD, STYLE), BeardStyleType::valueOf),
            parseOneOf(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
            parseOneOf(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
            parseOneOf(parameters, combine(HAIR, STYLE), HairStyle::valueOf),
            parseOneOf(parameters, combine(SHORT, HAIR, STYLE), ShortHairStyle::valueOf),
            parseOneOf(parameters, combine(LONG, HAIR, STYLE), LongHairStyle::valueOf),
            parseOneOf(parameters, combine(HAIR, LENGTH), HairLength::valueOf),
            parseOneOf(parameters, LIP_COLORS, Color::valueOf),
        ),
        parseClothingStyles(parameters),
        parseHolidays(parameters)
    )
}

fun parseNamingConvention(
    parameters: Parameters,
): NamingConvention {
    return when (parameters[NAMING_CONVENTION]) {
        Mononym.toString() -> MononymConvention(parseNamesByGender(parameters, NAMES))

        Family.toString() -> FamilyConvention(
            parse(parameters, combine(NAME, ORDER), GivenNameFirst),
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

fun parseClothingStyles(
    parameters: Parameters,
): GenderMap<FashionId?> {
    val female = parseFashionId(parameters, Gender.Female)
    val genderless = parseFashionId(parameters, Gender.Genderless)
    val male = parseFashionId(parameters, Gender.Male)

    return GenderMap(female, genderless, male)
}

private fun parseFashionId(
    parameters: Parameters,
    gender: Gender,
) = parseOptionalInt(parameters, "$FASHION-$gender")?.let { FashionId(it) }
