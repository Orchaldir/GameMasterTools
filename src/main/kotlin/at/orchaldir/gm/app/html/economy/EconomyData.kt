package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseCurrencyId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyData
import at.orchaldir.gm.core.model.economy.job.IncomeType
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.economy.money.print
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.core.selector.realm.getPopulationsWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showEconomyData(
    call: ApplicationCall,
    state: State,
    economy: EconomyData,
) {
    val currency = state.getDefaultCurrency()

    h2 { +"Economy" }

    fieldLink("Default Currency", call, state, economy.defaultCurrency)
    field("Default Income Type", economy.defaultIncomeType)

    table {
        tr {
            th { +"Name" }
            thMultiLines(listOf("Max", "Yearly", "Income"))
            th { +"Districts" }
            th { +"Jobs" }
            th { +"Realms" }
            th { +"Towns" }
        }
        economy.standardsOfLiving.forEach { standard ->
            tr {
                tdLink(call, state, standard)
                td { +currency.print(standard.maxYearlyIncome) }
                tdSkipZero(getPopulationsWith(state.getDistrictStorage(), standard.id))
                tdSkipZero(state.countJobs(standard.id))
                tdSkipZero(getPopulationsWith(state.getRealmStorage(), standard.id))
                tdSkipZero(getPopulationsWith(state.getTownStorage(), standard.id))
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEconomyData(
    state: State,
    economy: EconomyData,
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

fun parseEconomyData(
    state: State,
    parameters: Parameters,
) = EconomyData(
    parseCurrencyId(parameters, CURRENCY),
    parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined),
    parseList(parameters, STANDARD, 1) { index, param ->
        parseStandardOfLiving(state, StandardOfLivingId(index), parameters, param)
    },
)
