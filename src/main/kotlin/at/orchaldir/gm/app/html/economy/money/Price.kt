package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showTooltip
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.selector.economy.money.getAmountPerDenomination
import at.orchaldir.gm.core.selector.economy.money.print
import at.orchaldir.gm.core.selector.getDefaultCurrency
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayPrice(
    call: ApplicationCall,
    currency: Currency,
    price: Price,
    showZero: Boolean = false,
) {
    if (showZero || price.value > 0) {
        link(call, currency, currency.print(price))
    }
}

fun HtmlBlockTag.fieldPrice(
    state: State,
    label: String,
    price: Price,
) {
    val currency = state.getDefaultCurrency()
    field(label, currency.print(price))
}

// edit

fun HtmlBlockTag.selectPrice(
    state: State,
    label: String,
    price: Price,
    param: String,
    min: Int,
    max: Int,
    ) {
    val currency = state.getDefaultCurrency()
    field(label) {
        selectInt(price.value, min, max, 1, param)
        +" = "
        +currency.print(price)
    }
}

// parse

fun parsePrice(parameters: Parameters, param: String, default: Int = 0) = Price(
    parseInt(parameters, param, default),
)
