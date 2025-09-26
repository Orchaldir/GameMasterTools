package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // battle
        is DeleteBattle -> deleteElement(state, action.id, State::canDeleteBattle)
        is UpdateBattle -> UPDATE_BATTLE(state, action)
        // catastrophe
        is DeleteCatastrophe -> deleteElement(state, action.id, State::canDeleteCatastrophe)
        is UpdateCatastrophe -> UPDATE_CATASTROPHE(state, action)
        // district
        is DeleteDistrict -> deleteElement(state, action.id, State::canDeleteDistrict)
        is UpdateDistrict -> UPDATE_DISTRICT(state, action)
        // legal code
        is DeleteLegalCode -> deleteElement(state, action.id, State::canDeleteLegalCode)
        is UpdateLegalCode -> UPDATE_LEGAL_CODE(state, action)
        // realm
        is DeleteRealm -> deleteElement(state, action.id, State::canDeleteRealm)
        is UpdateRealm -> UPDATE_REALM(state, action)
        // town
        is DeleteTown -> deleteElement(state, action.id, State::canDeleteTown)
        is UpdateTown -> UPDATE_TOWN(state, action)
        // treaty
        is DeleteTreaty -> deleteElement(state, action.id, State::canDeleteTreaty)
        is UpdateTreaty -> UPDATE_TREATY(state, action)
        // war
        is DeleteWar -> deleteElement(state, action.id, State::canDeleteWar)
        is UpdateWar -> UPDATE_WAR(state, action)
    }
}
