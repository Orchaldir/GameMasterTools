package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteRealm(realm: RealmId) = true

fun State.getExistingRealms(date: Date?) = getExistingElements(getRealmStorage().getAll(), date)

