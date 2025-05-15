package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner

fun State.canDeleteRealm(realm: RealmId) = !isCreator(realm)
        && !isCurrentOrFormerOwner(realm)
        && countWars(realm) == 0

fun State.getExistingRealms(date: Date?) = getExistingElements(getRealmStorage().getAll(), date)

