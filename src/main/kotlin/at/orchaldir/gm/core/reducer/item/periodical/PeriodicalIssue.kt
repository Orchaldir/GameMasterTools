package at.orchaldir.gm.core.reducer.item.periodical

import at.orchaldir.gm.core.action.CreatePeriodicalIssue
import at.orchaldir.gm.core.action.UpdatePeriodicalIssue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.selector.time.date.getStartDay
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERIODICAL_ISSUE: Reducer<CreatePeriodicalIssue, State> = { state, _ ->
    val periodical = PeriodicalIssue(state.getPeriodicalIssueStorage().nextId)

    noFollowUps(state.updateStorage(state.getPeriodicalIssueStorage().add(periodical)))
}

val UPDATE_PERIODICAL_ISSUE: Reducer<UpdatePeriodicalIssue, State> = { state, action ->
    val issue = action.issue
    state.getPeriodicalIssueStorage().require(issue.id)

    validatePeriodicalIssue(state, issue)

    noFollowUps(state.updateStorage(state.getPeriodicalIssueStorage().update(issue)))
}

fun validatePeriodicalIssue(
    state: State,
    issue: PeriodicalIssue,
) {
    val periodical = state.getPeriodicalStorage().getOrThrow(issue.periodical)

    require(isDateValid(state, issue, periodical)) {
        "The Issue ${issue.id.value} cannot be published before the start of the periodical!"
    }

    issue.articles.forEach { state.getArticleStorage().require(it) }
}

private fun isDateValid(
    state: State,
    issue: PeriodicalIssue,
    periodical: Periodical,
) = if (periodical.date != null) {
    val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

    calendar.getStartDay(issue.date) >= calendar.getStartDay(periodical.date)
} else {
    true
}