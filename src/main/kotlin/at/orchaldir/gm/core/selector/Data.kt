package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getStartYear

fun State.getDefaultCurrencyId() = data.defaultCurrency

fun State.getDefaultCurrency() = getCurrencyStorage().getOrThrow(getDefaultCurrencyId())

