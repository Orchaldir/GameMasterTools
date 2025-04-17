package at.orchaldir.gm.app.html.model.item.periodical

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.FREQUENCY
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.time.parseCalendarId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.html.model.parseLanguageId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PublicationFrequency
import at.orchaldir.gm.core.selector.item.getValidPublicationFrequencies
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showPeriodical(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    optionalField(call, state, "Date", periodical.startDate())
    fieldCreator(call, state, periodical.founder, "Founder")
    showOwnership(call, state, periodical.ownership)
    fieldLink("Language", call, state, periodical.language)
    fieldLink("Calendar", call, state, periodical.calendar)
    field("Frequency", periodical.frequency)
}

// edit

fun FORM.editPeriodical(
    state: State,
    periodical: Periodical,
) {
    selectComplexName(state, periodical.name)
    selectOptionalDate(state, "Start", periodical.startDate(), DATE)
    selectCreator(state, periodical.founder, periodical.id, periodical.startDate(), "Founder")
    selectOwnership(state, periodical.ownership, periodical.startDate())
    selectElement(state, "Language", LANGUAGE, state.getLanguageStorage().getAll(), periodical.language)
    selectElement(state, "Calendar", CALENDAR, state.getCalendarStorage().getAll(), periodical.calendar, true)
    selectPublicationFrequency(state, periodical)
}

private fun FORM.selectPublicationFrequency(
    state: State,
    periodical: Periodical,
) {
    val frequencies = state.getValidPublicationFrequencies(periodical.calendar)
    selectValue("Frequency", FREQUENCY, frequencies, periodical.frequency)
}

// parse

fun parsePeriodicalId(value: String) = PeriodicalId(value.toInt())

fun parsePeriodicalId(parameters: Parameters, param: String) = PeriodicalId(parseInt(parameters, param))

fun parsePeriodical(parameters: Parameters, state: State, id: PeriodicalId): Periodical {
    val startDate = parseOptionalDate(parameters, state, DATE)

    return Periodical(
        id,
        parseComplexName(parameters),
        startDate,
        parseCreator(parameters),
        parseOwnership(parameters, state, startDate),
        parseLanguageId(parameters, LANGUAGE),
        parseCalendarId(parameters, CALENDAR),
        parse(parameters, FREQUENCY, PublicationFrequency.Daily),
    )
}
