package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.countCharactersKilledInWar
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteWar(war: WarId) = countCharactersKilledInWar(war) == 0
        && countRealmsDestroyedByWar(war) == 0
        && countBattles(war) == 0

fun State.countWars(realm: RealmId) = getWarStorage()
    .getAll()
    .count { it.realms.contains(realm) }

fun State.getWars(realm: RealmId) = getWarStorage()
    .getAll()
    .filter { it.realms.contains(realm) }

fun State.getExistingWars(date: Date?) = getExistingElements(getWarStorage().getAll(), date)