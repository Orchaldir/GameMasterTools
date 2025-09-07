package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.canDeleteDestroyer
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.world.getRegionsCreatedBy

fun State.canDeleteBattle(battle: BattleId) = DeleteResult(battle)
    .addElements(getRegionsCreatedBy(battle))
    .apply { canDeleteDestroyer(battle, it) }

fun State.countBattlesLedBy(character: CharacterId) = getBattleStorage()
    .getAll()
    .count { it.isLedBy(character) }

fun State.countBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .count { it.isParticipant(realm) }

fun State.countBattles(war: WarId) = getBattleStorage()
    .getAll()
    .count { it.war == war }

fun State.getBattlesLedBy(character: CharacterId) = getBattleStorage()
    .getAll()
    .filter { it.isLedBy(character) }

fun State.getBattles(realm: RealmId) = getBattleStorage()
    .getAll()
    .filter { it.isParticipant(realm) }

fun State.getBattles(war: WarId) = getBattleStorage()
    .getAll()
    .filter { it.war == war }

fun State.getExistingBattles(date: Date?) = getExistingElements(getBattleStorage().getAll(), date)