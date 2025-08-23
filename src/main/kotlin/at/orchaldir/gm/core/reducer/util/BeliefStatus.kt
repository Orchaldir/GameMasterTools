package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Atheist
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.UndefinedBeliefStatus
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.util.WorshipOfPantheon
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
    date: Date?,
) {
    when (status) {
        Atheist, UndefinedBeliefStatus -> doNothing()
        is WorshipOfGod -> state.getGodStorage()
            .require(status.god) { "The $noun's god ${status.god.value} doesn't exist!" }

        is WorshipOfPantheon -> state.getPantheonStorage()
            .require(status.pantheon) { "The $noun's pantheon ${status.pantheon.value} doesn't exist!" }
    }
}