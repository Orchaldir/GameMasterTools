package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.economy.parseJobId
import at.orchaldir.gm.app.html.economy.parseOptionalBusinessId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseSettlementId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.job.EmployerType
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingSettlements
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEmployees(
    call: ApplicationCall,
    state: State,
    employees: Collection<Character>,
    label: String = "Employees",
    showOptionalBusiness: Boolean = true,
    showSettlement: Boolean = true,
) {
    fieldList(label, state.sortCharacters(employees)) { employee ->
        link(call, state, employee)
        +" as "
        showEmploymentStatus(
            call,
            state,
            employee.employmentStatus.current,
            showOptionalBusiness = showOptionalBusiness,
            showSettlement = showSettlement,
        )
    }
}

fun HtmlBlockTag.showEmploymentStatusHistory(
    call: ApplicationCall,
    state: State,
    ownership: History<EmploymentStatus>,
) = showHistory(call, state, ownership, "Employment Status", HtmlBlockTag::showEmploymentStatus)

fun HtmlBlockTag.showEmploymentStatus(
    call: ApplicationCall,
    state: State,
    status: EmploymentStatus,
    showUndefined: Boolean = true,
    showOptionalBusiness: Boolean = true,
    showSettlement: Boolean = true,
) {
    when (status) {
        is Employed -> {
            link(call, state, status.job)
            +" at "
            link(call, state, status.business)
        }

        is EmployedByRealm -> {
            link(call, state, status.job)

            if (showSettlement) {
                +" of "
                link(call, state, status.realm)
            }
        }

        is EmployedBySettlement -> if (showSettlement) {
            if (status.optionalBusiness != null && showOptionalBusiness) {
                link(call, state, status.job)
                +" at "
                link(call, state, status.settlement)
                +"'s "
                link(call, state, status.optionalBusiness)
            } else {
                link(call, state, status.job)
                +" of "
                link(call, state, status.settlement)
            }
        } else {
            if (status.optionalBusiness != null && showOptionalBusiness) {
                link(call, state, status.job)
                +" at "
                link(call, state, status.optionalBusiness)
            } else {
                link(call, state, status.job)
            }
        }

        Retired -> +"Retired"
        Unemployed -> +"Unemployed"
        UndefinedEmploymentStatus -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun HtmlBlockTag.selectEmploymentStatusHistory(
    state: State,
    ownership: History<EmploymentStatus>,
    startDate: Date,
) = selectHistory(
    state,
    EMPLOYMENT,
    ownership,
    "Employment Status",
    startDate,
    null,
    HtmlBlockTag::selectEmploymentStatus
)

fun HtmlBlockTag.selectEmploymentStatus(
    state: State,
    param: String,
    status: EmploymentStatus,
    start: Date?,
) {
    selectValue("Employment Status", param, EmploymentStatusType.entries, status.getType())

    when (status) {
        Retired, UndefinedEmploymentStatus, Unemployed -> doNothing()

        is Employed -> {
            selectElement(
                state,
                combine(param, BUSINESS),
                state.getOpenBusinesses(start),
                status.business,
            )
            selectJob(state, param, EmployerType.Business, status.job)
        }

        is EmployedByRealm -> {
            selectElement(
                state,
                combine(param, REALM),
                state.getExistingRealms(start),
                status.realm,
            )
            selectJob(state, param, EmployerType.Realm, status.job)
        }

        is EmployedBySettlement -> {
            selectElement(
                state,
                combine(param, SETTLEMENT),
                state.getExistingSettlements(start),
                status.settlement,
            )
            selectJob(state, param, EmployerType.Settlement, status.job)
            selectOptionalElement(
                state,
                "Business",
                combine(param, BUSINESS),
                state.getOpenBusinesses(start),
                status.optionalBusiness,
            )
        }
    }
}

private fun HtmlBlockTag.selectJob(
    state: State,
    param: String,
    employerType: EmployerType,
    job: JobId,
) = selectElement(
    state,
    combine(param, JOB),
    state.getJobs(employerType),
    job,
)

// parse

fun parseEmploymentStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, EMPLOYMENT, state, startDate, ::parseEmploymentStatus)

fun parseEmploymentStatus(parameters: Parameters, state: State, param: String): EmploymentStatus {
    return when (parse(parameters, param, EmploymentStatusType.Undefined)) {
        EmploymentStatusType.Employed -> Employed(
            parseBusinessId(parameters, combine(param, BUSINESS)),
            parseJobId(parameters, combine(param, JOB)),
        )

        EmploymentStatusType.EmployedByRealm -> EmployedByRealm(
            parseJobId(parameters, combine(param, JOB)),
            parseRealmId(parameters, combine(param, REALM)),
        )

        EmploymentStatusType.EmployedBySettlement -> EmployedBySettlement(
            parseJobId(parameters, combine(param, JOB)),
            parseSettlementId(parameters, combine(param, SETTLEMENT)),
            parseOptionalBusinessId(parameters, combine(param, BUSINESS)),
        )

        EmploymentStatusType.Retired -> Retired
        EmploymentStatusType.Unemployed -> Unemployed
        EmploymentStatusType.Undefined -> UndefinedEmploymentStatus
    }
}