package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isDestroyer

fun State.canDeleteWar(war: WarId) = !isDestroyer(war)
        && countBattles(war) == 0

fun State.countWars(realm: RealmId) = getWarStorage()
    .getAll()
    .count { true } //TODO: it.realms.contains(realm) }

fun State.getWars(realm: RealmId) = getWarStorage()
    .getAll()
    .filter { true } //TODO: it.realms.contains(realm) }

fun State.getExistingWars(date: Date?) = getExistingElements(getWarStorage().getAll(), date)

fun State.getWarsEndedBy(catastrophe: CatastropheId) = getWarStorage()
    .getAll()
    .filter { it.status.isEndedBy(catastrophe) }

fun State.getWarsEndedBy(treaty: TreatyId) = getWarStorage()
    .getAll()
    .filter { it.status.treaty() == treaty }