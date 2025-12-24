package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.selector.economy.money.getAmountPerDenomination
import at.orchaldir.gm.core.selector.economy.money.print
import at.orchaldir.gm.core.selector.getDefaultCurrency
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayPrice(
    call: ApplicationCall,
    currency: Currency,
    price: Price,
    showZero: Boolean = false,
) {
    if (showZero || price.value > 0) {
        showTooltip(currency.name()) {
            link(call, currency, currency.print(price))
        }
    }
}

fun HtmlBlockTag.fieldPrice(
    call: ApplicationCall,
    state: State,
    label: String,
    price: Price?,
) {
    if (price != null && price.value > 0) {
        val currency = state.getDefaultCurrency()

        field(label) {
            displayPrice(call, currency, price, true)
        }
    }
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
    var isHighestAvailable = true

    showDetails(label, true) {
        currency.getAmountPerDenomination(price).forEach { (denomination, amount) ->
            if (amount > 0) {
                isHighestAvailable = false
            }

            field(denomination.text.text) {
                selectInt(
                    amount,
                    if (isHighestAvailable) {
                        0
                    } else {
                        -1
                    },
                    Int.MAX_VALUE,
                    1,
                    combine(param, denomination.text.text),
                )
            }
        }
    }
}

// parse

fun parsePrice(state: State, parameters: Parameters, param: String): Price {
    val currency = state.getDefaultCurrency()
    val denominations = currency.getDenominations()
        .map { parseInt(parameters, combine(param, it.text.text), 0) }

    return currency.calculatePriceFromDenominations(denominations)
}
