package at.orchaldir.gm.core.reducer.item.periodical

import at.orchaldir.gm.core.action.CreatePeriodical
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodical
import at.orchaldir.gm.core.selector.item.periodical.getValidPublicationFrequencies
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERIODICAL: Reducer<CreatePeriodical, State> = { state, _ ->
    val periodical = Periodical(state.getPeriodicalStorage().nextId)

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().add(periodical)))
}

val UPDATE_PERIODICAL: Reducer<UpdatePeriodical, State> = { state, action ->
    val periodical = action.periodical
    validatePeriodical(state, periodical)

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().update(periodical)))
}

fun validatePeriodical(
    state: State,
    periodical: Periodical,
) {
    val date = periodical.date

    state.getPeriodicalStorage().require(periodical.id)
    state.getCalendarStorage().require(periodical.calendar)
    state.getLanguageStorage().require(periodical.language)
    validateFrequency(state, periodical)
    checkDate(state, date, "Founding")
    checkOwnership(state, periodical.ownership, date)
}

private fun validateFrequency(state: State, periodical: Periodical) {
    val type = periodical.frequency

    require(state.getValidPublicationFrequencies(periodical.calendar).contains(type)) {
        "The Calendar ${periodical.calendar.value} doesn't support $type!"
    }
}