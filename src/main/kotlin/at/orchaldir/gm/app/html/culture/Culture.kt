package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.time.editHolidays
import at.orchaldir.gm.app.html.time.parseCalendarId
import at.orchaldir.gm.app.html.time.parseHolidays
import at.orchaldir.gm.app.html.time.showHolidays
import at.orchaldir.gm.app.html.util.editDataSources
import at.orchaldir.gm.app.html.util.name.parseNameListId
import at.orchaldir.gm.app.html.util.parseDataSources
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.parseLanguageId
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showCreated
import at.orchaldir.gm.app.html.util.showDataSources
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.NamingConventionType.*
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showCulture(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    fieldLink("Calendar", call, state, culture.calendar)
    showRarityMap("Languages", culture.languages) { l ->
        link(call, state, l)
    }
    showHolidays(call, state, culture.holidays)
    showDataSources(call, state, culture.sources)
    showNamingConvention(culture.namingConvention, call, state)
    showClothingOptions(call, state, culture)
    showUsages(call, state, culture)
    showCreated(call, state, culture.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    h2 { +"Usage" }

    fieldList(call, state, state.getCharacters(culture.id))
}

private fun HtmlBlockTag.showNamingConvention(
    namingConvention: NamingConvention,
    call: ApplicationCall,
    state: State,
) {
    h2 { +"Naming Convention" }
    field("Type", namingConvention.getType())
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

private fun HtmlBlockTag.showGenonymConvention(
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

private fun HtmlBlockTag.showNamesByGender(
    call: ApplicationCall,
    state: State,
    label: String,
    namesByGender: GenderMap<NameListId>,
) {
    showGenderMap(label, namesByGender) { id ->
        link(call, state, id)
    }
}

private fun HtmlBlockTag.showStyleByGender(
    label: String,
    namesByGender: GenderMap<String>,
) {
    showGenderMap(label, namesByGender) { text ->
        +text
    }
}

private fun HtmlBlockTag.showClothingOptions(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }
    showGenderMap(culture.fashion) { id ->
        optionalLink(call, state, id)
    }
}

// edit

fun FORM.editCulture(
    state: State,
    culture: Culture,
) {
    selectName(culture.name)
    selectElement(state, "Calendar", CALENDAR_TYPE, state.getCalendarStorage().getAll(), culture.calendar)
    selectRarityMap(
        "Languages",
        LANGUAGES,
        state.getLanguageStorage(),
        culture.languages,
    ) { it.name.text }
    editHolidays(state, culture.holidays)
    editDataSources(state, culture.sources)
    editNamingConvention(culture.namingConvention, state)
    editClothingOptions(state, culture)
}

private fun FORM.editNamingConvention(
    namingConvention: NamingConvention,
    state: State,
) {
    h2 { +"Naming Convention" }
    selectValue("Type", NAMING_CONVENTION, NamingConventionType.entries, namingConvention.getType())

    when (namingConvention) {
        is FamilyConvention -> {
            selectValue("Name Order", combine(NAME, ORDER), NameOrder.entries, namingConvention.nameOrder)
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
    selectValue("Genonymic Style", GENONYMIC_STYLE, GenonymicStyleType.entries, style.getType())

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
    selectGenderMap(fieldLabel, namesByGender, param) { genderParam, nameListId ->
        selectNameList(genderParam, state, nameListId)
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
                label = nameList.name.text
                value = nameList.id.value.toString()
                selected = nameList.id == nameListId
            }
        }
    }
}

private fun FORM.selectWordsByGender(label: String, genderMap: GenderMap<String>, param: String) {
    selectGenderMap(label, genderMap, param) { genderParam, word ->
        textInput(name = genderParam) {
            value = word
        }
    }
}

private fun FORM.editClothingOptions(
    state: State,
    culture: Culture,
) {
    h2 { +"Fashion" }

    selectGenderMap(culture.fashion, FASHION) { genderParam, fashionId ->
        selectOptionalElement(
            state,
            genderParam,
            state.getFashionStorage().getAll(),
            fashionId,
        )
    }
}

// parse

fun parseCultureId(parameters: Parameters, param: String) = CultureId(parseInt(parameters, param))
fun parseCultureId(value: String) = CultureId(value.toInt())

fun parseCulture(
    parameters: Parameters,
    id: CultureId,
) = Culture(
    id,
    parseName(parameters),
    parseCalendarId(parameters, CALENDAR_TYPE),
    parseSomeOf(parameters, LANGUAGES, ::parseLanguageId),
    parseNamingConvention(parameters),
    parseClothingStyles(parameters),
    parseHolidays(parameters),
    parseDataSources(parameters),
)

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
) = parseGenderMap(param) { genderParam ->
    parseNameListId(parameters, genderParam)
}

fun parseWordsByGender(
    parameters: Parameters,
    param: String,
) = parseGenderMap(param) { genderParam ->
    parameters[genderParam] ?: "Unknown"
}

fun parseClothingStyles(
    parameters: Parameters,
) = parseGenderMap(FASHION) { param ->
    parseOptionalFashionId(parameters, param)
}
