package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isDestroyer
import at.orchaldir.gm.core.selector.world.getRegionsCreatedBy

fun State.canDeleteCatastrophe(catastrophe: CatastropheId) = !isDestroyer(catastrophe)
        && getHolidays(catastrophe).isEmpty()
        && getRegionsCreatedBy(catastrophe).isEmpty()
        && getWarsEndedBy(catastrophe).isEmpty()

fun State.getExistingCatastrophes(date: Date?) = getExistingElements(getCatastropheStorage().getAll(), date)