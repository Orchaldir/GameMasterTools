package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.item.getTexts

fun State.canDeleteFont(id: FontId) = DeleteResult(id)
    .addElements(getCurrencyUnits(id))
    .addElements(getTexts(id))

