package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.economy.parseJobId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldEmploymentStatus(
    call: ApplicationCall,
    state: State,
    employmentStatus: EmploymentStatus,
) {
    field("Employment Status") {
        showEmploymentStatus(call, state, employmentStatus)
    }
}

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

fun FORM.selectEmploymentStatus(
    state: State,
    character: Character,
) {
    val employmentStatus = character.employmentStatus
    selectValue("Employment Status", EMPLOYMENT, EmploymentStatusType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == employmentStatus.getType()
    }
    when (employmentStatus) {
        Unemployed -> doNothing()

        is Employed -> {
            selectValue("Business", combine(EMPLOYMENT, BUSINESS), state.getBusinessStorage().getAll()) { business ->
                label = business.name
                value = business.id.value.toString()
                selected = employmentStatus.business == business.id
            }
            selectValue("Job", combine(EMPLOYMENT, JOB), state.getJobStorage().getAll()) { job ->
                label = job.name
                value = job.id.value.toString()
                selected = employmentStatus.job == job.id
            }
        }
    }
}

fun parseEmploymentStatus(parameters: Parameters): EmploymentStatus {
    return when (parse(parameters, EMPLOYMENT, EmploymentStatusType.Unemployed)) {
        EmploymentStatusType.Employed -> Employed(
            parseBusinessId(parameters, combine(EMPLOYMENT, BUILDING)),
            parseJobId(parameters, combine(EMPLOYMENT, JOB)),
        )

        else -> Unemployed
    }
}