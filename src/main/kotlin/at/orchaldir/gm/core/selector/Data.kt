package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State

fun State.getDefaultCurrencyId() = data.defaultCurrency

fun State.getDefaultCurrency() = getCurrencyStorage().getOrThrow(getDefaultCurrencyId())

