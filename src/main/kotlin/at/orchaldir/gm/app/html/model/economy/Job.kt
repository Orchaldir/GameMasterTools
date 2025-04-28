package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.religion.getGodsAssociatedWith
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortDomains
import at.orchaldir.gm.core.selector.util.sortGods
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
    showSalary(state, job.income)
    showRarityMap("Spells", job.spells) { spell ->
        link(call, state, spell)
    }

    showJobUsage(call, state, job)
}

private fun HtmlBlockTag.showSalary(
    state: State,
    income: Income,
) {
    if (income is Salary) {
        showPrice(state, "Average Salary", income.salary)
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

    fieldList(call, state, state.getBusinesses(job.id))
    fieldList("Current Characters", state.sortCharacters(characters)) { (character, name) ->
        link(call, character.id, name)
    }
    fieldList("Previous Characters", state.sortCharacters(previousCharacters)) { (character, name) ->
        link(call, character.id, name)
    }
    fieldList(call, state, "Associated Domains", state.sortDomains(domains))
    fieldList(
        call,
        state,
        "Associated Gods",
        state.sortGods(gods)
    )
}

// edit

fun FORM.editJob(
    state: State,
    job: Job,
) {
    selectName(job.name)
    editSalary(state, job.income)
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), job.spells, false) { it.name.text }
}

private fun HtmlBlockTag.editSalary(
    state: State,
    income: Income,
) {
    selectValue(
        "Income Type",
        combine(PRICE, TYPE),
        IncomeType.entries,
        income.getType(),
        true
    )
    if (income is Salary) {
        editPrice(state, "Average Salary", income.salary, PRICE, 1, 100000)
    }
}

// parse

fun parseJobId(parameters: Parameters, param: String) = JobId(parseInt(parameters, param))

fun parseJobId(value: String) = JobId(value.toInt())

fun parseJob(id: JobId, parameters: Parameters) = Job(
    id,
    parseName(parameters),
    parseIncome(parameters),
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
)

fun parseIncome(parameters: Parameters) =
    when (parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined)) {
        IncomeType.Undefined -> UndefinedIncome
        IncomeType.Salary -> Salary(
            parsePrice(parameters, PRICE)
        )
    }
