package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.doNothing

fun checkEmploymentStatusHistory(
    state: State,
    history: History<EmploymentStatus>,
    startDate: Date,
) = checkHistory(state, history, startDate, "employment", ::checkEmploymentStatus)

private fun checkEmploymentStatus(
    state: State,
    status: EmploymentStatus,
    noun: String,
    date: Date,
) {
    when (status) {
        UndefinedEmploymentStatus -> doNothing()
        Unemployed -> doNothing()
        is Employed -> checkEmployed(state, noun, date, status.job, status.business)
        is EmployedByTown -> {
            checkEmployed(state, noun, date, status.job, status.optionalBusiness)
            state.requireExists(state.getTownStorage(), status.town, date)
        }
    }
}

private fun checkEmployed(
    state: State,
    noun: String,
    date: Date,
    jobId: JobId,
    businessId: BusinessId?,
) {
    if (businessId != null) {
        state.requireExists(state.getBusinessStorage(), businessId, date)
    }
    state.getJobStorage().require(jobId)
}