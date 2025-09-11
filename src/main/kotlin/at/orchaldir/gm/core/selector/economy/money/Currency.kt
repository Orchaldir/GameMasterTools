package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.Denomination
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.item.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.item.getTextsMadeOf
import at.orchaldir.gm.core.selector.realm.getRealmsWithCurrency
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCurrency
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf

fun State.canDeleteCurrency(currency: CurrencyId) = DeleteResult(currency)
    .addElements(getCurrencyUnits(currency))
    .addElements(getRealmsWithCurrency(currency))
    .addElements(getRealmsWithPreviousCurrency(currency))

// get

fun State.getExistingCurrency(date: Date?) = getExistingElements(getCurrencyStorage().getAll(), date)

// display

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

    return if (remains > 0) {
        denomination.display(times) + " " + display(remains)
    } else {
        denomination.display(times)
    }
}
