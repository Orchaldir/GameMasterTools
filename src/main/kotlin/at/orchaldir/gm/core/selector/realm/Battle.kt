package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.selector.getHolidays

fun State.canDeleteBattle(battle: BattleId) = true

fun State.countBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .count { it.participants.any { it.realm == realm } }

fun State.countBattles(war: WarId) = getBattleStorage()
    .getAll()
    .count { it.war == war }

fun State.getBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .filter { it.participants.any { it.realm == realm } }

fun State.getBattles(war: WarId) = getBattleStorage()
    .getAll()
    .filter { it.war == war }