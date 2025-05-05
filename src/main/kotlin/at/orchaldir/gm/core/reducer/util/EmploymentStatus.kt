package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Employed
import at.orchaldir.gm.core.model.character.EmployedByTown
import at.orchaldir.gm.core.model.character.EmploymentStatus
import at.orchaldir.gm.core.model.character.UndefinedEmploymentStatus
import at.orchaldir.gm.core.model.character.Unemployed
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
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
        is Employed -> checkEmployed(state, noun, date, employmentStatus.job, employmentStatus.business)
        is EmployedByTown -> checkEmployed(state, noun, date, employmentStatus.job, employmentStatus.optionalBusiness)
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
        val business = state.getBusinessStorage()
            .getOrThrow(businessId) { "The $noun's business doesn't exist!" }
        require(state.exists(business, date)) { "The $noun's business is not in operation!" }
    }
    state.getJobStorage().require(jobId) { "The $noun's job doesn't exist!" }
}