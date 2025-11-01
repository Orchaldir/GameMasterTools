package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.editPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.showPrice
import at.orchaldir.gm.app.html.item.parseOptionalUniformId
import at.orchaldir.gm.app.html.magic.parseSpellId
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.util.parseGenderMap
import at.orchaldir.gm.app.html.util.selectGenderMap
import at.orchaldir.gm.app.html.util.showGenderMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.realm.getRealms
import at.orchaldir.gm.core.selector.realm.getTowns
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.religion.getGodsAssociatedWith
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showJob(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    field("Employer Type", job.employerType)
    showSalary(call, state, job.income)
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

private fun HtmlBlockTag.showSalary(
    call: ApplicationCall,
    state: State,
    income: Income,
) {
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> fieldLink(call, state, income.standard)
        is Salary -> showPrice(state, "Average Yearly Salary", income.yearlySalary)
    }
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
    state: State,
    job: Job,
) {
    selectName(job.name)
    selectValue("Employer Type", EMPLOYMENT, EmployerType.entries, job.employerType)
    editSalary(state, job.income)
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
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), job.spells) { it.name.text }
}

private fun HtmlBlockTag.editSalary(
    state: State,
    income: Income,
) {
    selectValue(
        "Income Type",
        combine(PRICE, TYPE),
        state.data.economy.defaultIncomeType.getValidTypes(),
        income.getType(),
    )
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> selectElement(
            state,
            STANDARD,
            state.data.economy.standardsOfLiving,
            income.standard,
        )

        is Salary -> editPrice(state, "Average Yearly Salary", income.yearlySalary, PRICE, 1, 100000)
    }
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
    parseIncome(parameters),
    parse<Gender>(parameters, GENDER),
    parseElements(parameters, STATISTIC, ::parseStatisticId),
    parseGenderMap(UNIFORM) { param ->
        parseOptionalUniformId(parameters, param)
    },
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
)

fun parseIncome(parameters: Parameters) =
    when (parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined)) {
        IncomeType.Undefined -> UndefinedIncome
        IncomeType.StandardOfLiving -> AffordableStandardOfLiving(
            parseStandardOfLivingId(parameters, STANDARD),
        )

        IncomeType.Salary -> Salary(
            parsePrice(parameters, PRICE)
        )

    }
