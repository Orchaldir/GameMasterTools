package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.getHolidays
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteBattle(battle: BattleId) = true

fun State.countBattlesLedBy(character: CharacterId) = getBattleStorage()
    .getAll()
    .count { it.participants.any { it.leader == character } }

fun State.countBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .count { it.participants.any { it.realm == realm } }

fun State.countBattles(war: WarId) = getBattleStorage()
    .getAll()
    .count { it.war == war }

fun State.getBattlesLedBy(character: CharacterId) = getBattleStorage()
    .getAll()
    .filter { it.participants.any { it.leader == character } }

fun State.getBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .filter { it.participants.any { it.realm == realm } }

fun State.getBattles(war: WarId) = getBattleStorage()
    .getAll()
    .filter { it.war == war }

fun State.getExistingBattles(date: Date?) = getExistingElements(getBattleStorage().getAll(), date)