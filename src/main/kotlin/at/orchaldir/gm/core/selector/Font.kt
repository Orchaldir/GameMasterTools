package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits

fun State.canDelete(font: FontId) = countCurrencyUnits(font) == 0

