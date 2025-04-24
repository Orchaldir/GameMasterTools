package at.orchaldir.gm.app.html.model.item.periodical

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.FREQUENCY
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.time.parseCalendarId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicalIssues
import at.orchaldir.gm.core.selector.item.periodical.getValidPublicationFrequencies
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
    fieldCreator(call, state, periodical.founder, "Founder")
    showOwnership(call, state, periodical.ownership)
    fieldLink("Language", call, state, periodical.language)
    fieldLink("Calendar", call, state, periodical.calendar)
    showFrequency(call, state, periodical)

    h2 { +"Usage" }

    showList("Issues", state.sortPeriodicalIssues(state.getPeriodicalIssues(periodical.id))) { issue ->
        link(call, issue.id, issue.dateAsName(state))
    }
}

private fun HtmlBlockTag.showFrequency(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    field("Frequency", periodical.frequency.getType())
    optionalField(call, state, periodical.calendar, "Publication Start", periodical.frequency.getStartDate())
}

// edit

fun FORM.editPeriodical(
    state: State,
    periodical: Periodical,
) {
    val date = periodical.startDate(state)
    selectComplexName(state, periodical.name)
    selectCreator(state, periodical.founder, periodical.id, date, "Founder")
    selectOwnership(state, periodical.ownership, date)
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

    selectValue("Frequency", FREQUENCY, frequencies, periodical.frequency.getType(), true)

    when (val frequency = periodical.frequency) {
        is DailyPublication -> selectDay("Start Day", calendar, frequency.start, DATE)
        is WeeklyPublication -> selectWeek("Start Week", calendar, frequency.start, DATE)
        is MonthlyPublication -> selectMonth("Start Month", calendar, frequency.start, DATE)
        is YearlyPublication -> selectYear("Start Year", calendar, frequency.start, DATE)
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
        PublicationFrequencyType.Daily -> DailyPublication(parseDay(parameters, calendar, DATE))
        PublicationFrequencyType.Weekly -> WeeklyPublication(parseWeek(parameters, calendar, DATE))
        PublicationFrequencyType.Monthly -> MonthlyPublication(parseMonth(parameters, calendar, DATE))
        PublicationFrequencyType.Yearly -> YearlyPublication(parseYear(parameters, calendar, DATE))
    }
