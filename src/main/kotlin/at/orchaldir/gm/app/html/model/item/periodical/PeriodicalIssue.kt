package at.orchaldir.gm.app.html.model.item.periodical

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.PERIODICAL
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.selector.time.calendar.getCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showPeriodicalIssue(
    call: ApplicationCall,
    state: State,
    issue: PeriodicalIssue,
) {
    fieldLink("Periodical", call, state, issue.periodical)
    optionalField(call, state, "Publication Date", issue.date)
}

// edit

fun FORM.editPeriodicalIssue(
    state: State,
    issue: PeriodicalIssue,
) {
    val calendar = state.getCalendar(issue.periodical)
    selectElement(state, "Periodical", PERIODICAL, state.getPeriodicalStorage().getAll(), issue.periodical, true)
    selectOptionalDate(calendar, "Publication Date", issue.date, DATE)
}

// parse

fun parsePeriodicalIssueId(value: String) = PeriodicalIssueId(value.toInt())

fun parsePeriodicalIssueId(parameters: Parameters, param: String) = PeriodicalIssueId(parseInt(parameters, param))

fun parsePeriodicalIssue(parameters: Parameters, state: State, id: PeriodicalIssueId): PeriodicalIssue {
    val periodicalId = parsePeriodicalId(parameters, PERIODICAL)
    val calendar = state.getCalendar(periodicalId)

    return PeriodicalIssue(
        id,
        periodicalId,
        parseOptionalDate(parameters, calendar, DATE),
    )
}
