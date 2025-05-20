package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.parseBusinessId
import at.orchaldir.gm.app.html.model.economy.parseJobId
import at.orchaldir.gm.app.html.model.economy.parseOptionalBusinessId
import at.orchaldir.gm.app.html.model.realm.parseRealmId
import at.orchaldir.gm.app.html.model.realm.parseTownId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.job.EmployerType
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingTowns
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEmployees(
    call: ApplicationCall,
    state: State,
    employees: Collection<Character>,
    label: String = "Employees",
    showOptionalBusiness: Boolean = true,
    showTown: Boolean = true,
) {
    fieldList(label, state.sortCharacters(employees)) { employee ->
        link(call, state, employee)
        +" as "
        showEmploymentStatus(
            call,
            state,
            employee.employmentStatus.current,
            showOptionalBusiness = showOptionalBusiness,
            showTown = showTown,
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
    showTown: Boolean = true,
) {
    when (status) {
        is Employed -> {
            link(call, state, status.job)
            +"at "
            link(call, state, status.business)
        }

        is EmployedByRealm -> {
            link(call, state, status.job)

            if (showTown) {
                +" of "
                link(call, state, status.realm)
            }
        }

        is EmployedByTown -> if (showTown) {
            if (status.optionalBusiness != null && showOptionalBusiness) {
                link(call, state, status.job)
                +"at "
                link(call, state, status.town)
                +"'s "
                link(call, state, status.optionalBusiness)
            } else {
                link(call, state, status.job)
                +" of "
                link(call, state, status.town)
            }
        } else {
            if (status.optionalBusiness != null && showOptionalBusiness) {
                link(call, state, status.job)
                +"at "
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

fun FORM.selectEmploymentStatusHistory(
    state: State,
    ownership: History<EmploymentStatus>,
    startDate: Date,
) = selectHistory(state, EMPLOYMENT, ownership, startDate, "Employment Status", HtmlBlockTag::selectEmploymentStatus)

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
                "Business",
                combine(param, BUSINESS),
                state.sortBusinesses(state.getOpenBusinesses(start)),
                status.business,
            )
            selectJob(state, param, EmployerType.Business, status.job)
        }

        is EmployedByRealm -> {
            selectElement(
                state,
                "Realm",
                combine(param, REALM),
                state.sortRealms(state.getExistingRealms(start)),
                status.realm,
            )
            selectJob(state, param, EmployerType.Realm, status.job)
        }

        is EmployedByTown -> {
            selectElement(
                state,
                "Town",
                combine(param, TOWN),
                state.sortTowns(state.getExistingTowns(start)),
                status.town,
            )
            selectJob(state, param, EmployerType.Town, status.job)
            selectOptionalElement(
                state,
                "Business",
                combine(param, BUSINESS),
                state.sortBusinesses(state.getOpenBusinesses(start)),
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
    "Job",
    combine(param, JOB),
    state.sortJobs(state.getJobs(employerType)),
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

        EmploymentStatusType.EmployedByTown -> EmployedByTown(
            parseJobId(parameters, combine(param, JOB)),
            parseTownId(parameters, combine(param, TOWN)),
            parseOptionalBusinessId(parameters, combine(param, BUSINESS)),
        )

        EmploymentStatusType.Retired -> Retired
        EmploymentStatusType.Unemployed -> Unemployed
        EmploymentStatusType.Undefined -> UndefinedEmploymentStatus
    }
}