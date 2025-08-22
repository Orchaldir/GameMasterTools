package at.orchaldir.gm.app.html.item.periodical

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.FREQUENCY
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseLanguageId
import at.orchaldir.gm.app.html.time.parseCalendarId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PublicationFrequency
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicalIssues
import at.orchaldir.gm.core.selector.item.periodical.getValidPublicationFrequencies
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.core.selector.util.sortPeriodicalIssues
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showPeriodical(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)
    showOwnership(call, state, periodical.ownership)
    fieldLink("Language", call, state, periodical.language)
    fieldLink("Calendar", call, state, periodical.calendar)
    optionalField(call, state, periodical.calendar, "Publication Start", periodical.startDate())
    field("Frequency", periodical.frequency)

    h2 { +"Usage" }

    fieldList("Issues", state.sortPeriodicalIssues(state.getPeriodicalIssues(periodical.id))) { issue ->
        link(call, issue.id, display(calendar, issue.date))
    }
}

// edit

fun FORM.editPeriodical(
    state: State,
    periodical: Periodical,
) {
    val date = periodical.startDate()
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)
    val frequencies = getValidPublicationFrequencies(calendar)

    selectName(periodical.name)
    selectOwnership(state, periodical.ownership, date)
    selectElement(state, LANGUAGE, state.getLanguageStorage().getAll(), periodical.language)
    selectElement(state, CALENDAR, state.getCalendarStorage().getAll(), periodical.calendar)
    selectOptionalDate(calendar, "Date", periodical.date, DATE)
    selectValue("Frequency", FREQUENCY, frequencies, periodical.frequency)
}

// parse

fun parsePeriodicalId(value: String) = PeriodicalId(value.toInt())

fun parsePeriodicalId(parameters: Parameters, param: String) = PeriodicalId(parseInt(parameters, param))

fun parsePeriodical(parameters: Parameters, state: State, id: PeriodicalId): Periodical {
    val startDate = parseOptionalDate(parameters, state, DATE)
    val calendarId = parseCalendarId(parameters, CALENDAR)
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)

    return Periodical(
        id,
        parseName(parameters),
        parseOwnership(parameters, state, startDate),
        parseLanguageId(parameters, LANGUAGE),
        calendarId,
        parseOptionalDate(parameters, calendar, DATE),
        parse(parameters, FREQUENCY, PublicationFrequency.Daily),
    )
}
