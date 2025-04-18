package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.UndefinedEmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.util.exists
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
        UndefinedEmploymentStatus -> doNothing()
        Unemployed -> doNothing()
        is Employed -> {
            val business = state.getBusinessStorage()
                .getOrThrow(employmentStatus.business) { "The $noun's business doesn't exist!" }
            require(state.exists(business, date)) { "The $noun's business is not in operation!" }
            state.getJobStorage().require(employmentStatus.job) { "The $noun's job doesn't exist!" }
        }
    }
}