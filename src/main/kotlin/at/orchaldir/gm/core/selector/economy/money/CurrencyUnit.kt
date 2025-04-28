package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.material.MaterialId

fun State.canDeleteCurrencyUnit(id: CurrencyUnitId) = true

fun State.countCurrencyUnits(currency: CurrencyId) = getCurrencyUnitStorage()
    .getAll()
    .count { unit -> unit.currency == currency }

fun State.getCurrencyUnits(currency: CurrencyId) = getCurrencyUnitStorage()
    .getAll()
    .filter { unit -> unit.currency == currency }

fun State.getCurrencyUnits(material: MaterialId) = getCurrencyUnitStorage()
    .getAll()
    .filter { it.format.contains(material) }

