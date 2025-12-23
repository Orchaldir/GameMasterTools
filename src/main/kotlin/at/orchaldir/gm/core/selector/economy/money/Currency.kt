package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.realm.getRealmsWithCurrency
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCurrency
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteCurrency(currency: CurrencyId) = DeleteResult(currency)
    .addElements(getCurrencyUnits(currency))
    .addElements(getRealmsWithCurrency(currency))
    .addElements(getRealmsWithPreviousCurrency(currency))

// get

fun State.getExistingCurrency(date: Date?) = getExistingElements(getCurrencyStorage().getAll(), date)

// price

fun Currency.getAmountPerDenomination(price: Price) = getAmountPerDenomination(price.value)

private fun Currency.getAmountPerDenomination(price: Int): List<Pair<Denomination, Int>> {
    var remaining = price
    val result: MutableList<Pair<Denomination, Int>> = mutableListOf()
    var denomination = denomination

    subDenominations.reversed().forEach { (subdenomination, threshold) ->
        if (price > threshold) {
            val times = remaining / threshold
            remaining %= threshold

            result.add(Pair(denomination, times))
        }

        denomination = subdenomination
    }

    result.add(Pair(denomination, remaining))

    return result
}

// print

fun Currency.print(price: Price): String {
    var string = ""
    var isFirstAvailable = true
    var index = 0
    val amountPerDenomination = getAmountPerDenomination(price)

    amountPerDenomination.forEach { (denomination, number) ->
        val isLast = index == amountPerDenomination.size - 1
        val canSkipZero = !isLast || !isFirstAvailable

        if (number == 0 && canSkipZero) {
            return@forEach
        }
        else if (isFirstAvailable) {
            isFirstAvailable = false
        } else {
            string += " "
        }

        string += denomination.display(number)
        index++
    }

    return string
}
