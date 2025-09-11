package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.canDeleteDestroyer
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.world.getRegionsCreatedBy

fun State.canDeleteCatastrophe(catastrophe: CatastropheId) = DeleteResult(catastrophe)
    .addElements(getHolidays(catastrophe))
    .addElements(getRegionsCreatedBy(catastrophe))
    .addElements(getWarsEndedBy(catastrophe))
    .apply { canDeleteDestroyer(catastrophe, it) }

fun State.getExistingCatastrophes(date: Date?) = getExistingElements(getCatastropheStorage().getAll(), date)