package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.economy.isInOperation
import at.orchaldir.gm.core.selector.world.exists
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
            val business = state.getBusinessStorage()
                .getOrThrow(employmentStatus.business) { "The $noun's business doesn't exist!" }
            require(state.isInOperation(business, date)) { "The $noun's business is not in operation!" }
            state.getJobStorage().require(employmentStatus.job) { "The $noun's job doesn't exist!" }
        }
    }
}