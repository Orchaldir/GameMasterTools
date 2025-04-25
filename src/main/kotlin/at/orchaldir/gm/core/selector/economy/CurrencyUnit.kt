package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId

fun State.canDeleteCurrencyUnit(id: CurrencyUnitId) = true

fun State.getCurrencyUnits(currency: CurrencyId) = getCurrencyUnitStorage()
    .getAll()
    .filter { unit -> unit.currency == currency }

