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
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.item.getValidPublicationFrequencies
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun BODY.showPeriodical(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    fieldCreator(call, state, periodical.founder, "Founder")
    showOwnership(call, state, periodical.ownership)
    fieldLink("Language", call, state, periodical.language)
    fieldLink("Calendar", call, state, periodical.calendar)
    showFrequency(call, state, periodical.frequency)
}

private fun HtmlBlockTag.showFrequency(
    call: ApplicationCall,
    state: State,
    frequency: PublicationFrequency,
) {
    field("Frequency", frequency.getType())
    optionalField(call, state, "Publication Start", frequency.getStartDate())
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
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)
    val frequencies = getValidPublicationFrequencies(calendar)

    selectValue("Frequency", FREQUENCY, frequencies, periodical.frequency.getType())

    when (val frequency = periodical.frequency) {
        is DailyPublication -> selectOptionalDay(calendar, "Start Day", frequency.start, DATE)
        is WeeklyPublication -> selectOptionalWeek(calendar, "Start Week", frequency.start, DATE)
        is MonthlyPublication -> selectOptionalMonth(calendar, "Start Month", frequency.start, DATE)
        is YearlyPublication -> selectOptionalYear(calendar, "Start Year", frequency.start, DATE)
    }
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
        parseComplexName(parameters),
        parseCreator(parameters),
        parseOwnership(parameters, state, startDate),
        parseLanguageId(parameters, LANGUAGE),
        calendarId,
        parseFrequency(parameters, calendar),
    )
}

private fun parseFrequency(parameters: Parameters, calendar: Calendar) =
    when (parse(parameters, FREQUENCY, PublicationFrequencyType.Daily)) {
        PublicationFrequencyType.Daily -> DailyPublication(parseOptionalDay(parameters, calendar, DATE))
        PublicationFrequencyType.Weekly -> WeeklyPublication(parseOptionalWeek(parameters, calendar, DATE))
        PublicationFrequencyType.Monthly -> MonthlyPublication(parseOptionalMonth(parameters, calendar, DATE))
        PublicationFrequencyType.Yearly -> YearlyPublication(parseOptionalYear(parameters, calendar, DATE))
    }
