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

    when (val frequency = periodical.frequency) {
        is DailyPublication -> selectDay(
            "Publication Date",
            calendar,
            frequency.start + issue.number,
            DATE,
            frequency.start,
        )

        is WeeklyPublication -> selectWeek(
            "Publication Date",
            calendar,
            frequency.start + issue.number,
            DATE,
            frequency.start,
        )

        is MonthlyPublication -> selectMonth(
            "Publication Date",
            calendar,
            frequency.start + issue.number,
            DATE,
            frequency.start,
        )

        is YearlyPublication -> selectYear(
            "Publication Date",
            calendar,
            frequency.start + issue.number,
            DATE,
            frequency.start,
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

    return when (val frequency = periodical.frequency) {
        is DailyPublication -> parseDay(parameters, calendar, DATE).day - frequency.start.day
        is WeeklyPublication -> parseWeek(parameters, calendar, DATE).week - frequency.start.week
        is MonthlyPublication -> parseMonth(parameters, calendar, DATE).month - frequency.start.month
        is YearlyPublication -> parseYear(parameters, calendar, DATE).year - frequency.start.year
    }
}
