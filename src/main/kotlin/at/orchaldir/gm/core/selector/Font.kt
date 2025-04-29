package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.selector.economy.money.countCurrencyUnits
import at.orchaldir.gm.core.selector.item.countTexts

fun State.canDelete(font: FontId) = countCurrencyUnits(font) == 0
        && countTexts(font) == 0

