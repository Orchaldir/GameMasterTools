package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.utils.doNothing

fun checkEmploymentStatusHistory(
    state: State,
    history: History<EmploymentStatus>,
    startDate: Date,
) = checkHistory(state, history, startDate, "employment", ::checkEmploymentStatus)

private fun checkEmploymentStatus(
    state: State,
    employmentStatus: EmploymentStatus,
    noun: String,
    date: Date,
) {
    when (employmentStatus) {
        Unemployed -> doNothing()
        is Employed -> {
            state.getBusinessStorage().require(employmentStatus.business) { "The $noun's business doesn't exist!" }
            state.getJobStorage().require(employmentStatus.job) { "The $noun's job doesn't exist!" }
        }
    }
}