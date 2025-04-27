package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId

fun State.canDeleteCurrency(id: CurrencyId) = countCurrencyUnits(id) == 0
