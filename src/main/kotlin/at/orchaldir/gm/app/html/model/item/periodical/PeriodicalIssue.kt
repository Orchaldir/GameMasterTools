package at.orchaldir.gm.app.html.model.item.periodical

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.PERIODICAL
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Month
import at.orchaldir.gm.core.model.time.date.Week
import at.orchaldir.gm.core.model.time.date.Year
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPeriodicalIssue(
    call: ApplicationCall,
    state: State,
    issue: PeriodicalIssue,
) {
    val periodical = state.getPeriodicalStorage().getOrThrow(issue.periodical)
    fieldLink("Periodical", call, state, issue.periodical)
    field("Issue Number", issue.number)
    field(call, state, periodical.calendar, "Publication Date", issue.getDate(state))
}

// edit

fun FORM.editPeriodicalIssue(
    state: State,
    issue: PeriodicalIssue,
) {
    selectElement(state, "Periodical", PERIODICAL, state.getPeriodicalStorage().getAll(), issue.periodical, true)
    selectIssueNumber(state, issue)
}

private fun FORM.selectIssueNumber(
    state: State,
    issue: PeriodicalIssue,
) {
    val periodical = state.getPeriodicalStorage().getOrThrow(issue.periodical)
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

    when (periodical.frequency) {
        PublicationFrequency.Daily -> selectDay(
            "Publication Date",
            calendar,
            Day(issue.number),
            DATE,
            periodical.date,
        )

        PublicationFrequency.Weekly -> selectWeek(
            "Publication Date",
            calendar,
            Week(issue.number),
            DATE,
            periodical.date,
        )

        PublicationFrequency.Monthly -> selectMonth(
            "Publication Date",
            calendar,
            Month(issue.number),
            DATE,
            periodical.date,
        )

        PublicationFrequency.Yearly -> selectYear(
            "Publication Date",
            calendar,
            Year(issue.number),
            DATE,
            periodical.date,
        )
    }
}

// parse

fun parsePeriodicalIssueId(value: String) = PeriodicalIssueId(value.toInt())

fun parsePeriodicalIssueId(parameters: Parameters, param: String) = PeriodicalIssueId(parseInt(parameters, param))

fun parsePeriodicalIssue(parameters: Parameters, state: State, id: PeriodicalIssueId): PeriodicalIssue {
    val periodicalId = parsePeriodicalId(parameters, PERIODICAL)

    return PeriodicalIssue(
        id,
        periodicalId,
        parseIssueNumber(parameters, state, periodicalId),
    )
}

fun parseIssueNumber(parameters: Parameters, state: State, id: PeriodicalId): Int {
    val periodical = state.getPeriodicalStorage().getOrThrow(id)
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

    return when (periodical.frequency) {
        PublicationFrequency.Daily -> parseDay(parameters, calendar, DATE).day
        PublicationFrequency.Weekly -> parseWeek(parameters, calendar, DATE).week
        PublicationFrequency.Monthly -> parseMonth(parameters, calendar, DATE).month
        PublicationFrequency.Yearly -> parseYear(parameters, calendar, DATE).year
    }
}
