package at.orchaldir.gm.app.html.util.name

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.GivenNamesType.MaleAndFemale
import at.orchaldir.gm.core.model.culture.name.GivenNamesType.NonGendered
import at.orchaldir.gm.core.model.culture.name.NameOrder.GivenNameFirst
import at.orchaldir.gm.core.model.culture.name.NamingConventionType.*
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showNamingConvention(
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
            showGivenNames(call, state, namingConvention.givenNames)
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

        is MononymConvention -> showGivenNames(call, state, namingConvention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> showGenonymConvention(
            call,
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
        is RandomGivenAndLastName -> {
            showGivenNames(call, state, namingConvention.givenNames)
            showRarityMap("Middle Name Options", namingConvention.middleNameOptions)
            fieldLink("Last Names", call, state, namingConvention.lastNames)
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
    namingConvention: NamingConvention,
    state: State,
) {
    h2 { +"Naming Convention" }
    selectValue("Type", NAMING_CONVENTION, NamingConventionType.entries, namingConvention.getType())

    when (namingConvention) {
        is FamilyConvention -> {
            selectValue("Name Order", combine(NAME, ORDER), NameOrder.entries, namingConvention.nameOrder)
            editGivenNames(state, namingConvention.givenNames)
            selectRarityMap("Middle Name Options", combine(MIDDLE, NAME), namingConvention.middleNameOptions)
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

        is MononymConvention -> editGivenNames(state, namingConvention.names)

        NoNamingConvention -> doNothing()
        is PatronymConvention -> selectGenonymConvention(
            state,
            namingConvention.lookupDistance,
            namingConvention.style,
            namingConvention.names
        )
        is RandomGivenAndLastName -> {
            editGivenNames(state, namingConvention.givenNames)
            selectRarityMap("Middle Name Options", combine(MIDDLE, NAME), namingConvention.middleNameOptions)
            field("Last Names") {
                selectNameList(FAMILY_NAMES, state, namingConvention.lastNames)
            }
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
