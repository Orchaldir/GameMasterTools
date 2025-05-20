package at.orchaldir.gm.app.html.economy.money

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.selector.economy.money.display
import at.orchaldir.gm.core.selector.getDefaultCurrency
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPrice(
    state: State,
    label: String,
    price: Price,
) {
    val currency = state.getDefaultCurrency()
    field(label, currency.display(price))
}

// edit

fun HtmlBlockTag.editPrice(
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
        +currency.display(price)
    }
}

// parse

fun parsePrice(parameters: Parameters, param: String) = Price(
    parseInt(parameters, param, 1),
)
