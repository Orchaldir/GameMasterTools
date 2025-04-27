package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.economy.money.Price

fun State.canDeleteCurrency(id: CurrencyId) = countCurrencyUnits(id) == 0

fun Currency.display(price: Price) = display(price.value)

private fun Currency.display(price: Int): String {
    var lastThreshold = 0

    subDenominations.forEach { (subdenomination, threshold) ->
        if (price < threshold) {
            return display(subdenomination, price, lastThreshold)
        }

        lastThreshold = threshold
    }

    return display(denomination, price, lastThreshold)
}

private fun Currency.display(denomination: Denomination, price: Int, threshold: Int): String {
    if (threshold == 0) {
        return denomination.display(price)
    }

    val times = price / threshold
    val remains = price % threshold

    return denomination.display(times) + " " + display(remains)
}
