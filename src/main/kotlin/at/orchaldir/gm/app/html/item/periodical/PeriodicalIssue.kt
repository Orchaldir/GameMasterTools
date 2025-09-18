package at.orchaldir.gm.app.html.item.periodical

import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.PERIODICAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.field
import at.orchaldir.gm.app.html.util.parseDate
import at.orchaldir.gm.app.html.util.selectDate
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.selector.util.getExistingElements
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
    field(call, state, periodical.calendar, "Publication Date", issue.date)
    fieldIds(call, state, issue.articles)
}

// edit

fun FORM.editPeriodicalIssue(
    state: State,
    issue: PeriodicalIssue,
) {
    selectElement(state, PERIODICAL, state.getPeriodicalStorage().getAll(), issue.periodical)
    selectIssueNumber(state, issue)
    val possibleArticle = state.getExistingElements(state.getArticleStorage().getAll(), issue.date)
    selectElements(state, "Articles", CONTENT, possibleArticle, issue.articles)
}

private fun FORM.selectIssueNumber(
    state: State,
    issue: PeriodicalIssue,
) {
    val periodical = state.getPeriodicalStorage().getOrThrow(issue.periodical)
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)
    val dateTypes = periodical.frequency.getValidDateTypes(calendar)

    selectDate(
        calendar,
        "Publication Date",
        issue.date,
        DATE,
        periodical.date,
        null,
        dateTypes,
    )
}

// parse

fun parsePeriodicalIssueId(value: String) = PeriodicalIssueId(value.toInt())

fun parsePeriodicalIssueId(parameters: Parameters, param: String) = PeriodicalIssueId(parseInt(parameters, param))

fun parsePeriodicalIssue(parameters: Parameters, state: State, id: PeriodicalIssueId): PeriodicalIssue {
    val periodicalId = parsePeriodicalId(parameters, PERIODICAL)
    val periodical = state.getPeriodicalStorage().getOrThrow(periodicalId)
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

    return PeriodicalIssue(
        id,
        periodicalId,
        parseDate(parameters, calendar, DATE),
        parseElements(parameters, CONTENT) { parseArticleId(it) },
    )
}
