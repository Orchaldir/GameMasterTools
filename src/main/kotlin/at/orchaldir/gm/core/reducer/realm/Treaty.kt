package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.UpdateTreaty
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyParticipant
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_TREATY: Reducer<UpdateTreaty, State> = { state, action ->
    val treaty = action.treaty
    state.getTreatyStorage().require(treaty.id)

    validateTreaty(state, treaty)

    noFollowUps(state.updateStorage(state.getTreatyStorage().update(treaty)))
}

fun validateTreaty(state: State, treaty: Treaty) {
    checkDate(state, treaty.date, "Treaty")
    state.getDataSourceStorage().require(treaty.sources)
    treaty.participants.forEach { validateParticipant(state, it, treaty.date) }
}

private fun validateParticipant(state: State, participant: TreatyParticipant, date: Date?) {
    state.requireExists(state.getRealmStorage(), participant.realm, date)

    if (participant.signature != null) {
        state.requireExists(state.getCharacterStorage(), participant.signature, date)
    }
}
