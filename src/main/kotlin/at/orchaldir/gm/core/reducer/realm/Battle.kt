package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.util.requireExists

fun validateParticipant(state: State, participant: BattleParticipant, date: Date?) {
    state.requireExists(state.getRealmStorage(), participant.realm, date)

    if (participant.leader != null) {
        state.requireExists(state.getCharacterStorage(), participant.leader, date)
    }
}
