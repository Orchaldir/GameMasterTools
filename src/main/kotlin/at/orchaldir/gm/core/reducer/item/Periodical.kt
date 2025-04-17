package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreatePeriodical
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.reducer.util.checkComplexName
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOwnershipWithOptionalDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.item.canDeletePeriodical
import at.orchaldir.gm.core.selector.item.getValidPublicationFrequencies
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERIODICAL: Reducer<CreatePeriodical, State> = { state, _ ->
    val periodical = Periodical(state.getPeriodicalStorage().nextId)

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().add(periodical)))
}

val DELETE_PERIODICAL: Reducer<DeletePeriodical, State> = { state, action ->
    state.getPeriodicalStorage().require(action.id)
    require(state.canDeletePeriodical(action.id)) { "The periodical ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().remove(action.id)))
}

val UPDATE_PERIODICAL: Reducer<UpdatePeriodical, State> = { state, action ->
    val periodical = action.periodical

    state.getPeriodicalStorage().require(periodical.id)
    state.getCalendarStorage().require(periodical.calendar)
    state.getLanguageStorage().require(periodical.language)
    checkComplexName(state, periodical.name)
    checkDate(state, periodical.startDate(), "Founding")
    validateCreator(state, periodical.founder, periodical.id, periodical.startDate(), "Founder")
    validateFrequency(state, periodical)
    checkOwnershipWithOptionalDate(state, periodical.ownership, periodical.startDate())

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().update(periodical)))
}

private fun validateFrequency(state: State, periodical: Periodical) {
    require(state.getValidPublicationFrequencies(periodical.calendar).contains(periodical.frequency)) {
        "The Calendar ${periodical.calendar.value} doesn't support weeks!"
    }
}