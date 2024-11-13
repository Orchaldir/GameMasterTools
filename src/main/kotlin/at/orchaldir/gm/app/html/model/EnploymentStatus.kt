package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.economy.parseJobId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.economy.isInOperation
import at.orchaldir.gm.core.selector.sortCharacters
import at.orchaldir.gm.core.selector.world.exists
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showEmployees(
    call: ApplicationCall,
    state: State,
    label: String,
    employees: Collection<Character>,
) {
    showList(label, state.sortCharacters(employees)) { (character, name) ->
        link(call, character.id, name)
        +" as "
        if (character.employmentStatus.current is Employed) {
            link(call, state, character.employmentStatus.current.job)
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
    employmentStatus: EmploymentStatus,
) {
    when (employmentStatus) {
        is Employed -> {
            link(call, state, employmentStatus.job)
            +" at "
            link(call, state, employmentStatus.business)
        }

        Unemployed -> +"Unemployed"
    }
}

fun FORM.selectEmploymentStatusHistory(
    state: State,
    ownership: History<EmploymentStatus>,
    startDate: Date,
) = selectHistory(state, EMPLOYMENT, ownership, startDate, "Employment Status", HtmlBlockTag::selectEmploymentStatus)

fun HtmlBlockTag.selectEmploymentStatus(
    state: State,
    param: String,
    employmentStatus: EmploymentStatus,
    start: Date,
) {
    selectValue("Employment Status", param, EmploymentStatusType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == employmentStatus.getType()
    }
    when (employmentStatus) {
        Unemployed -> doNothing()

        is Employed -> {
            selectValue("Business", combine(param, BUSINESS), state.getBusinessStorage().getAll()) { business ->
                label = business.name
                value = business.id.value.toString()
                selected = employmentStatus.business == business.id
                disabled = !state.isInOperation(business, start)
            }
            selectValue("Job", combine(param, JOB), state.getJobStorage().getAll()) { job ->
                label = job.name
                value = job.id.value.toString()
                selected = employmentStatus.job == job.id
            }
        }
    }
}

fun parseEmploymentStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, EMPLOYMENT, state, startDate, ::parseEmploymentStatus)

fun parseEmploymentStatus(parameters: Parameters, state: State, param: String): EmploymentStatus {
    return when (parse(parameters, param, EmploymentStatusType.Unemployed)) {
        EmploymentStatusType.Employed -> Employed(
            parseBusinessId(parameters, combine(param, BUILDING)),
            parseJobId(parameters, combine(param, JOB)),
        )

        else -> Unemployed
    }
}