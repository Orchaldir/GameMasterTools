package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.doNothing

fun checkBeliefStatusHistory(
    state: State,
    history: History<BeliefStatus>,
    startDate: Date?,
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
            .require(status.god) { "The $noun's ${status.god.print()} doesn't exist!" }

        is WorshipOfPantheon -> state.getPantheonStorage()
            .require(status.pantheon) { "The $noun's ${status.pantheon.print()} doesn't exist!" }
    }
}