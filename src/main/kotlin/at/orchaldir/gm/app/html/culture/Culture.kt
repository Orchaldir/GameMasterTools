package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.FASHION
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.population.showPopulationOfCulture
import at.orchaldir.gm.app.html.time.editHolidays
import at.orchaldir.gm.app.html.time.parseCalendarId
import at.orchaldir.gm.app.html.time.parseHolidays
import at.orchaldir.gm.app.html.time.showHolidays
import at.orchaldir.gm.app.html.util.name.editNamingConvention
import at.orchaldir.gm.app.html.util.name.parseNamingConvention
import at.orchaldir.gm.app.html.util.name.showNamingConvention
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showCreated
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showCulture(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    fieldLink("Calendar", call, state, culture.calendar)
    showRarityMap("Languages", culture.languages, true) {
        link(call, state, it)
    }
    showHolidays(call, state, culture.holidays)
    showDataSources(call, state, culture.sources)
    showNamingConvention(call, state, culture.namingConvention)
    showClothingOptions(call, state, culture)
    showPopulationOfCulture(call, state, culture)
    showUsages(call, state, culture.id)
    showCreated(call, state, culture.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    culture: CultureId,
) {
    val characters = state.getCharacters(culture)
    val templates = state.getCharacterTemplates(culture)

    if (characters.isEmpty() && templates.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, templates)
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

fun HtmlBlockTag.editCulture(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    selectName(culture.name)
    selectElement(state, CALENDAR_TYPE, state.getCalendarStorage().getAll(), culture.calendar)
    selectRarityMap(
        "Languages",
        LANGUAGES,
        state.getLanguageStorage(),
        culture.languages,
    )
    editHolidays(state, culture.holidays)
    editDataSources(state, culture.sources)
    editNamingConvention(state, culture.namingConvention)
    editClothingOptions(state, culture)
}

private fun HtmlBlockTag.editClothingOptions(
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
fun parseOptionalCultureId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { CultureId(it) }

fun parseCulture(
    state: State,
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

fun parseClothingStyles(
    parameters: Parameters,
) = parseGenderMap(FASHION) { param ->
    parseOptionalFashionId(parameters, param)
}
