package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.EmployerType
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.doNothing

fun checkEmploymentStatusHistory(
    state: State,
    history: History<EmploymentStatus>,
    startDate: Date,
) = validateHistory(state, history, startDate, "employment", ::checkEmploymentStatus)

private fun checkEmploymentStatus(
    state: State,
    status: EmploymentStatus,
    noun: String,
    date: Date?,
) {
    when (status) {
        Retired, UndefinedEmploymentStatus, Unemployed -> doNothing()
        is Employed -> checkEmployed(state, date, EmployerType.Business, status.job, status.business)
        is EmployedByRealm -> {
            checkEmployed(state, date, EmployerType.Realm, status.job, null)
            state.requireExists(state.getRealmStorage(), status.realm, date)
        }

        is EmployedBySettlement -> {
            checkEmployed(state, date, EmployerType.Settlement, status.job, status.optionalBusiness)
            state.requireExists(state.getSettlementStorage(), status.settlement, date)
        }
    }
}

private fun checkEmployed(
    state: State,
    date: Date?,
    employerType: EmployerType,
    jobId: JobId,
    businessId: BusinessId?,
) {
    if (businessId != null) {
        state.requireExists(state.getBusinessStorage(), businessId, date)
    }
    val job = state.getJobStorage().getOrThrow(jobId)
    require(job.employerType == employerType) { "Job ${jobId.value} has the wrong type of employer!" }
}