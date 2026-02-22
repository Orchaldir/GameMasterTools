package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseCurrencyId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyConfig
import at.orchaldir.gm.core.model.economy.job.IncomeType
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.countJobs
import at.orchaldir.gm.core.selector.economy.money.print
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.core.selector.realm.calculateTotalPopulation
import at.orchaldir.gm.core.selector.realm.getPopulationsWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showEconomyConfig(
    call: ApplicationCall,
    state: State,
    config: EconomyConfig,
) {
    val currency = state.getDefaultCurrency()

    h2 { +"Economy" }

    fieldLink("Default Currency", call, state, config.defaultCurrency)
    field("Default Income Type", config.defaultIncomeType)

    table {
        tr {
            th { +"Name" }
            thMultiLines(listOf("Max", "Yearly", "Income"))
            th { +"Districts" }
            th { +"Jobs" }
            th { +"Population" }
            th { +"Realms" }
            th { +"Settlements" }
        }
        config.standardsOfLiving.forEach { standard ->
            tr {
                tdLink(call, state, standard)
                td { +currency.print(standard.maxYearlyIncome) }
                tdSkipZero(getPopulationsWith(state.getDistrictStorage(), standard.id))
                tdSkipZero(state.countJobs(standard.id))
                tdSkipZero(state.calculateTotalPopulation({ it.getPopulation(standard.id) }))
                tdSkipZero(getPopulationsWith(state.getRealmStorage(), standard.id))
                tdSkipZero(getPopulationsWith(state.getSettlementStorage(), standard.id))
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEconomyConfig(
    state: State,
    config: EconomyConfig,
) {
    h2 { +"Economy" }

    selectElement(
        state,
        "Default Currency",
        CURRENCY,
        state.getCurrencyStorage().getAll(),
        config.defaultCurrency,
    )
    selectValue(
        "Default Income Type",
        combine(PRICE, TYPE),
        IncomeType.entries,
        config.defaultIncomeType,
    )

    var minIncome = 0

    editList(
        "Standards of Living",
        STANDARD,
        config.standardsOfLiving,
        1,
        10,
        1,
    ) { index, param, standard ->
        editStandardOfLiving(state, standard, param, minIncome)
        minIncome = standard.maxYearlyIncome.value
    }
}

// parse

fun parseEconomyConfig(
    state: State,
    parameters: Parameters,
) = EconomyConfig(
    parseCurrencyId(parameters, CURRENCY),
    parse(parameters, combine(PRICE, TYPE), IncomeType.Undefined),
    parseList(parameters, STANDARD, 1) { index, param ->
        parseStandardOfLiving(state, StandardOfLivingId(index), parameters, param)
    },
)
