package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State

fun State.getDefaultCurrencyId() = data.economy.defaultCurrency

fun State.getDefaultCurrency() = getCurrencyStorage().getOrThrow(getDefaultCurrencyId())

