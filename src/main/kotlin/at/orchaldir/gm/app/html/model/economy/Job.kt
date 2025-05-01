package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.money.editPrice
import at.orchaldir.gm.app.html.model.economy.money.parsePrice
import at.orchaldir.gm.app.html.model.economy.money.showPrice
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.religion.getGodsAssociatedWith
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortDomains
import at.orchaldir.gm.core.selector.util.sortGods
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
    showSalary(call, state, job.income)
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
        is Salary -> showPrice(state, "Average Salary", income.salary)
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
    fieldList(call, state, "Current Characters", state.sortCharacters(characters))
    fieldList(call, state, "Previous Characters", state.sortCharacters(previousCharacters))
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
    when (income) {
        UndefinedIncome -> doNothing()
        is AffordableStandardOfLiving -> selectElement(
            state,
            "Standard of Living",
            STANDARD,
            state.data.economy.standardsOfLiving,
            income.standard,
        )

        is Salary -> editPrice(state, "Average Salary", income.salary, PRICE, 1, 100000)
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
        IncomeType.StandardOfLiving -> AffordableStandardOfLiving(
            parseStandardOfLivingId(parameters, STANDARD),
        )
        IncomeType.Salary -> Salary(
            parsePrice(parameters, PRICE)
        )

    }
