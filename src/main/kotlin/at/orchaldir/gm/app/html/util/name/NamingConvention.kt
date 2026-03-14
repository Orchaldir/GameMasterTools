package at.orchaldir.gm.app.html.util.name

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.NamingConventionType.*
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.util.sortNameLists
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.textInput

// show

fun HtmlBlockTag.showNamingConvention(
    call: ApplicationCall,
    state: State,
    convention: NamingConvention,
) {
    h2 { +"Naming Convention" }
    field("Type", convention.getType())
    when (convention) {
        is FamilyConvention -> {
            field("Name Order", convention.nameOrder)
            showRarityMap("Middle Name Options", convention.middleNameOptions)
            showGivenNames(call, state, convention.givenNames)
            fieldLink("Family Names", call, state, convention.familyNames)
        }

        is GenonymConvention -> showGenonymConvention(
            call,
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is MatronymConvention -> showGenonymConvention(
            call,
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is MononymConvention -> showGivenNames(call, state, convention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> showGenonymConvention(
            call,
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is RandomGivenAndLastName -> {
            showGivenNames(call, state, convention.givenNames)
            showRarityMap("Middle Name Options", convention.middleNameOptions)
            fieldLink("Last Names", call, state, convention.lastNames)
        }
    }
}

private fun HtmlBlockTag.showGenonymConvention(
    call: ApplicationCall,
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GivenNames,
) {
    field("Lookup Distance", lookupDistance)
    field("Genonymic Style", style.javaClass.simpleName)
    when (style) {
        is ChildOfStyle -> showStyleByGender("Words", style.words)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> showStyleByGender("Prefix", style.prefix)
        is SuffixStyle -> showStyleByGender("Suffix", style.suffix)
    }
    showGivenNames(call, state, names)
}

private fun HtmlBlockTag.showStyleByGender(
    label: String,
    namesByGender: GenderMap<String>,
) {
    showGenderMap(label, namesByGender) { text ->
        +text
    }
}


// edit

fun HtmlBlockTag.editNamingConvention(
    state: State,
    convention: NamingConvention,
) {
    h2 { +"Naming Convention" }
    selectValue("Type", NAMING_CONVENTION, NamingConventionType.entries, convention.getType())

    when (convention) {
        is FamilyConvention -> {
            selectValue("Name Order", combine(NAME, ORDER), NameOrder.entries, convention.nameOrder)
            editGivenNames(state, convention.givenNames)
            selectRarityMap("Middle Name Options", combine(MIDDLE, NAME), convention.middleNameOptions)
            selectNameList(state, convention.familyNames)
        }

        is GenonymConvention -> selectGenonymConvention(
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is MatronymConvention -> selectGenonymConvention(
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is MononymConvention -> editGivenNames(state, convention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> selectGenonymConvention(
            state,
            convention.lookupDistance,
            convention.style,
            convention.names
        )

        is RandomGivenAndLastName -> {
            editGivenNames(state, convention.givenNames)
            selectRarityMap("Middle Name Options", combine(MIDDLE, NAME), convention.middleNameOptions)
            selectNameList(state, convention.lastNames)
        }
    }
}

private fun HtmlBlockTag.selectGenonymConvention(
    state: State,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    names: GivenNames,
) {
    selectValue("Lookup Distance", LOOKUP_DISTANCE, GenonymicLookupDistance.entries, lookupDistance)
    selectValue("Genonymic Style", GENONYMIC_STYLE, GenonymicStyleType.entries, style.getType())

    when (style) {
        is ChildOfStyle -> selectWordsByGender("Words", style.words, WORD)
        NamesOnlyStyle -> doNothing()
        is PrefixStyle -> selectWordsByGender("Prefix", style.prefix, WORD)
        is SuffixStyle -> selectWordsByGender("Suffix", style.suffix, WORD)
    }
    editGivenNames(state, names)
}

private fun HtmlBlockTag.selectNameList(
    state: State,
    nameListId: NameListId,
) {
    selectElement(
        state,
        "Family Names",
        FAMILY_NAMES,
        state.sortNameLists(),
        nameListId,
    )
}

private fun HtmlBlockTag.selectWordsByGender(label: String, genderMap: GenderMap<String>, param: String) {
    selectGenderMap(label, genderMap, param) { genderParam, word ->
        textInput(name = genderParam) {
            value = word
        }
    }
}

// parse

fun parseNamingConvention(parameters: Parameters) =
    when (parse(parameters, NAMING_CONVENTION, None)) {
        None -> NoNamingConvention
        Mononym -> MononymConvention(parseGivenNames(parameters))
        Random -> RandomGivenAndLastName(
            parseGivenNames(parameters),
            parseNameListId(parameters, FAMILY_NAMES),
            parseMiddleName(parameters),
        )

        Family -> FamilyConvention(
            parseGivenNames(parameters),
            parseNameListId(parameters, FAMILY_NAMES),
            parse(parameters, combine(NAME, ORDER), GivenNameFirst),
            parseMiddleName(parameters),
        )

        Patronym -> PatronymConvention(
            parseGivenNames(parameters),
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
        )

        Matronym -> MatronymConvention(
            parseGivenNames(parameters),
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
        )

        Genonym -> GenonymConvention(
            parseGivenNames(parameters),
            parse(parameters, LOOKUP_DISTANCE, GenonymicLookupDistance.OneGeneration),
            parseGenonymicStyle(parameters),
        )
    }

private fun parseMiddleName(parameters: Parameters) = parseOneOf(
    parameters,
    combine(MIDDLE, NAME),
    MiddleNameOption::valueOf,
    MiddleNameOption.entries,
)

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

fun parseWordsByGender(
    parameters: Parameters,
    param: String,
) = parseGenderMap(param) { genderParam ->
    parameters[genderParam] ?: "Unknown"
}
