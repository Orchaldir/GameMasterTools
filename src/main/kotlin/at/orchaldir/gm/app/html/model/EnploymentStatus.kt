package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.EMPLOYMENT
import at.orchaldir.gm.app.JOB
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.parseBusinessId
import at.orchaldir.gm.app.html.model.economy.parseJobId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.world.parseTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.util.sortBusinesses
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortJobs
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.getExistingTowns
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEmployees(
    call: ApplicationCall,
    state: State,
    label: String,
    employees: Collection<Character>,
) {
    fieldList(label, state.sortCharacters(employees)) { character ->
        link(call, state, character)
        +" as "
        when (val status = character.employmentStatus.current) {
            is Employed -> link(call, state, status.job)
            is EmployedByTown -> link(call, state, status.job)
            else -> doNothing()
        }
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
            +" at "
            link(call, state, status.business)
        }

        is EmployedByTown -> if (showTown) {
            if (status.optionalBusiness != null && showOptionalBusiness) {
                link(call, state, status.job)
                +" at "
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
                +" at "
                link(call, state, status.optionalBusiness)
            } else {
                link(call, state, status.job)
            }
        }

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
    employmentStatus: EmploymentStatus,
    start: Date?,
) {
    selectValue("Employment Status", param, EmploymentStatusType.entries, employmentStatus.getType(), true)

    when (employmentStatus) {
        UndefinedEmploymentStatus -> doNothing()
        Unemployed -> doNothing()

        is Employed -> {
            selectElement(
                state,
                "Business",
                combine(param, BUSINESS),
                state.sortBusinesses(state.getOpenBusinesses(start)),
                employmentStatus.business,
            )
            selectJob(state, param, employmentStatus.job)
        }

        is EmployedByTown -> {
            selectElement(
                state,
                "Town",
                combine(param, TOWN),
                state.sortTowns(state.getExistingTowns(start)),
                employmentStatus.town,
            )
            selectJob(state, param, employmentStatus.job)
            selectOptionalElement(
                state,
                "Business",
                combine(param, BUSINESS),
                state.sortBusinesses(state.getOpenBusinesses(start)),
                employmentStatus.optionalBusiness,
            )
        }
    }
}

private fun HtmlBlockTag.selectJob(
    state: State,
    param: String,
    job: JobId,
) = selectElement(
    state,
    "Job",
    combine(param, JOB),
    state.sortJobs(),
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

        EmploymentStatusType.EmployedByTown -> EmployedByTown(
            parseJobId(parameters, combine(param, JOB)),
            parseTownId(parameters, combine(param, TOWN)),
            parseBusinessId(parameters, combine(param, BUSINESS)),
        )

        EmploymentStatusType.Unemployed -> Unemployed
        EmploymentStatusType.Undefined -> UndefinedEmploymentStatus
    }
}