package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.doNothing

fun checkBeliefStatusHistory(
    state: State,
    history: History<BeliefStatus>,
    startDate: Date,
) = checkHistory(state, history, startDate, "belief", ::checkBeliefStatus)

private fun checkBeliefStatus(
    state: State,
    status: BeliefStatus,
    noun: String,
    date: Date,
) {
    when (status) {
        Atheist, UndefinedBeliefStatus -> doNothing()
        is WorshipsGod -> state.getGodStorage()
            .require(status.god) { "The $noun's god ${status.god.value} doesn't exist!" }

        is WorshipsPantheon -> state.getPantheonStorage()
            .require(status.pantheon) { "The $noun's pantheon ${status.pantheon.value} doesn't exist!" }
    }
}