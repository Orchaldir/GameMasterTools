package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.parseOptionalUniformId
import at.orchaldir.gm.app.html.magic.parseSpellId
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.economy.job.EmployerType
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.realm.getRealms
import at.orchaldir.gm.core.selector.realm.getTowns
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.religion.getGodsAssociatedWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showJob(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    field("Employer Type", job.employerType)
    showIncome(call, state, job.income)
    optionalField("Preferred Gender", job.preferredGender)
    fieldIds(call, state, "Important Statistics", job.importantStatistics)
    showGenderMap("Uniforms", job.uniforms) { uniform ->
        optionalLink(call, state, uniform)
    }
    showRarityMap("Spells", job.spells) { spell ->
        link(call, state, spell)
    }

    showJobUsage(call, state, job)
}

private fun HtmlBlockTag.showJobUsage(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    val characters = state.getEmployees(job.id).toSet()
    val previousCharacters = state.getPreviousEmployees(job.id).toSet() - characters
    val domains = state.getDomainsAssociatedWith(job.id)
    val gods = state.getGodsAssociatedWith(job.id)

    fieldIds(call, state, state.getBusinesses(job.id))
    fieldIds(call, state, state.getTowns(job.id))
    fieldIds(call, state, state.getRealms(job.id))
    fieldElements(call, state, "Current Characters", characters)
    fieldElements(call, state, "Previous Characters", previousCharacters)
    fieldElements(call, state, "Associated Domains", domains)
    fieldElements(
        call,
        state,
        "Associated Gods",
        gods,
    )
}

// edit

fun HtmlBlockTag.editJob(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    selectName(job.name)
    selectValue("Employer Type", EMPLOYMENT, EmployerType.entries, job.employerType)
    editIncome(state, job.income)
    selectOptionalValue("Preferred Gender", GENDER, job.preferredGender, Gender.entries)
    selectElements(
        state,
        "Important Statistics",
        STATISTIC,
        state.getStatisticStorage().getAll(),
        job.importantStatistics,
    )
    selectGenderMap("Uniforms", job.uniforms, UNIFORM) { genderParam, uniform ->
        selectOptionalElement(state, genderParam, state.getUniformStorage().getAll(), uniform)
    }
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), job.spells)
}

// parse

fun parseJobId(parameters: Parameters, param: String) = JobId(parseInt(parameters, param))

fun parseJobId(value: String) = JobId(value.toInt())

fun parseJob(
    state: State,
    parameters: Parameters,
    id: JobId,
) = Job(
    id,
    parseName(parameters),
    parse(parameters, EMPLOYMENT, EmployerType.Business),
    parseIncome(state, parameters),
    parse<Gender>(parameters, GENDER),
    parseElements(parameters, STATISTIC, ::parseStatisticId),
    parseGenderMap(UNIFORM) { param ->
        parseOptionalUniformId(parameters, param)
    },
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
)
