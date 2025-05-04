package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.editList
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.economy.money.parseCurrencyId
import at.orchaldir.gm.app.html.parseList
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.IncomeType
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.Economy
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.economy.money.display
import at.orchaldir.gm.core.selector.getDefaultCurrency
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showEconomy(
    call: ApplicationCall,
    state: State,
    economy: Economy,
) {
    val currency = state.getDefaultCurrency()

    h2 { +"Economy" }

    fieldLink("Default Currency", call, state, economy.defaultCurrency)
    field("Default Income Type", economy.defaultIncomeType)

    table {
        tr {
            th { +"Name" }
            th {
                +"Max"
                br { }
                +"Yearly Income"
            }
            th { +"Jobs" }
        }
        economy.standardsOfLiving.forEach { standard ->
            tr {
                td { link(call, state, standard) }
                td { +currency.display(standard.maxYearlyIncome) }
                tdSkipZero(state.countJobs(standard.id))
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEconomy(
    state: State,
    economy: Economy,
) {
    h2 { +"Economy" }

    selectElement(
        state,
        "Default Currency",
        CURRENCY,
        state.getCurrencyStorage().getAll(),
        economy.defaultCurrency,
    )
    selectValue(
        "Default Income Type",
        combine(PRICE, TYPE),
        IncomeType.entries,
        economy.defaultIncomeType,
    )

    var minIncome = 0

    editList(
        "Standards of Living",
        STANDARD,
        economy.standardsOfLiving,
        1,
        10,
        1,
    ) { index, param, standard ->
        editStandardOfLiving(state, standard, param, minIncome)
        minIncome = standard.maxYearlyIncome.value
    }
}

// parse

fun parseEconomy(
    parameters: Parameters,
) = Economy(
    parseCurrencyId(parameters, CURRENCY),
    parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined),
    parseList(parameters, STANDARD, 1) { index, param ->
        parseStandardOfLiving(StandardOfLivingId(index), parameters, param)
    },
)
