package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.economy.money.parseCurrencyId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.economy.Economy
import at.orchaldir.gm.core.selector.getDefaultCurrencyId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEconomy(
    call: ApplicationCall,
    state: State,
    economy: Economy,
) {
    h2 { +"Economy" }

    fieldLink("Default Currency", call, state, state.getDefaultCurrencyId())
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
}

// parse

fun parseEconomy(
    parameters: Parameters,
) = Economy(
    parseCurrencyId(parameters, CURRENCY),
    emptyList(),
)
