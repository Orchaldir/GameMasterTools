package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.editList
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.economy.money.parseCurrencyId
import at.orchaldir.gm.app.html.parseList
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.selector.economy.Economy
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

    table {
        tr {
            th { +"Name" }
            th { +"Cost per Day" }
        }
        economy.standardsOfLiving.forEach { standard ->
            tr {
                td { link(call, state, standard) }
                td { +currency.display(standard.costPerDay) }
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

    editList(
        "Standards of Living",
        PRICE,
        economy.standardsOfLiving,
        1,
        10,
        1,
    ) { index, param, standard ->
        editStandardOfLiving(state, standard, param)
    }
}

// parse

fun parseEconomy(
    parameters: Parameters,
) = Economy(
    parseCurrencyId(parameters, CURRENCY),
    parseList(parameters, PRICE, 1) { index, param ->
        parseStandardOfLiving(StandardOfLivingId(index), parameters, param)
    },
)
