package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreatePeriodicalIssue
import at.orchaldir.gm.core.action.DeletePeriodicalIssue
import at.orchaldir.gm.core.action.UpdatePeriodicalIssue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.selector.item.canDeletePeriodicalIssue
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERIODICAL_ISSUE: Reducer<CreatePeriodicalIssue, State> = { state, _ ->
    val periodical = PeriodicalIssue(state.getPeriodicalIssueStorage().nextId)

    noFollowUps(state.updateStorage(state.getPeriodicalIssueStorage().add(periodical)))
}

val DELETE_PERIODICAL_ISSUE: Reducer<DeletePeriodicalIssue, State> = { state, action ->
    state.getPeriodicalIssueStorage().require(action.id)
    require(state.canDeletePeriodicalIssue(action.id)) { "The periodical issue ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getPeriodicalIssueStorage().remove(action.id)))
}

val UPDATE_PERIODICAL_ISSUE: Reducer<UpdatePeriodicalIssue, State> = { state, action ->
    val issue = action.issue

    state.getPeriodicalIssueStorage().require(issue.id)
    state.getPeriodicalStorage().require(issue.periodical)

    noFollowUps(state.updateStorage(state.getPeriodicalIssueStorage().update(issue)))
}