package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.WarId

fun State.canDeleteWar(war: WarId) = true

fun State.countWars(realm: RealmId) = getWarStorage()
    .getAll()
    .count { it.realms.contains(realm) }

fun State.getWars(realm: RealmId) = getWarStorage()
    .getAll()
    .filter { it.realms.contains(realm) }
