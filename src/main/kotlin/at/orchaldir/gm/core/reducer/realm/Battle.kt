package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateBattle
import at.orchaldir.gm.core.action.DeleteBattle
import at.orchaldir.gm.core.action.UpdateBattle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.realm.canDeleteBattle
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BATTLE: Reducer<CreateBattle, State> = { state, _ ->
    val battle = Battle(state.getBattleStorage().nextId)

    noFollowUps(state.updateStorage(state.getBattleStorage().add(battle)))
}

val DELETE_BATTLE: Reducer<DeleteBattle, State> = { state, action ->
    state.getBattleStorage().require(action.id)

    validateCanDelete(state.canDeleteBattle(action.id), action.id, "it is used")

    noFollowUps(state.updateStorage(state.getBattleStorage().remove(action.id)))
}

val UPDATE_BATTLE: Reducer<UpdateBattle, State> = { state, action ->
    val battle = action.battle
    state.getBattleStorage().require(battle.id)

    validateBattle(state, battle)

    noFollowUps(state.updateStorage(state.getBattleStorage().update(battle)))
}

fun validateBattle(state: State, battle: Battle) {
    checkDate(state, battle.date, "Battle")
    state.getDataSourceStorage().require(battle.sources)
    battle.participants.forEach { validateParticipant(state, it, battle.date) }
}

private fun validateParticipant(state: State, participant: BattleParticipant, date: Date?) {
    state.requireExists(state.getRealmStorage(), participant.realm, date)

    if (participant.leader != null) {
        state.requireExists(state.getCharacterStorage(), participant.leader, date)
    }
}
