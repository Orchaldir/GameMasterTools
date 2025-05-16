package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner

fun State.canDeleteRealm(realm: RealmId) = !isCreator(realm)
        && !isCurrentOrFormerOwner(realm)
        && countWars(realm) == 0
        && getSubRealms(realm).isEmpty()
        && getPreviousSubRealms(realm).isEmpty()

fun State.getExistingRealms(date: Date?) = getExistingElements(getRealmStorage().getAll(), date)

fun State.getRealmsWithCapital(town: TownId) = getRealmStorage()
    .getAll()
    .filter { it.capital.current == town }

fun State.getRealmsWithPreviousCapital(town: TownId) = getRealmStorage()
    .getAll()
    .filter { it.capital.previousEntries.any { it.entry == town } }

fun State.getSubRealms(realm: RealmId) = getRealmStorage()
    .getAll()
    .filter { it.owner.current == realm }

fun State.getPreviousSubRealms(realm: RealmId) = getRealmStorage()
    .getAll()
    .filter { it.owner.previousEntries.any { it.entry == realm } }
