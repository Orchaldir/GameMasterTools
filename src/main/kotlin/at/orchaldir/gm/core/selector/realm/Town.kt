package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.core.selector.world.getTownMaps

fun State.canDeleteTown(town: TownId) = !isCurrentOrFormerOwner(town)
        && !isCreator(town)
        && getTownMaps(town).isEmpty()

// get

fun State.getExistingTowns(date: Date?) = getExistingElements(getTownStorage().getAll(), date)

fun State.getOwnedTowns(realm: RealmId) = getTownStorage()
    .getAll()
    .filter { it.owner.current == realm }

fun State.getPreviousOwnedTowns(realm: RealmId) = getTownStorage()
    .getAll()
    .filter { it.owner.previousEntries.any { it.entry == realm } }