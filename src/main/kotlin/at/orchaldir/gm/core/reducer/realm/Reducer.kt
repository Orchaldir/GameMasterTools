package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // catastrophe
        is CreateCatastrophe -> CREATE_CATASTROPHE(state, action)
        is DeleteCatastrophe -> DELETE_CATASTROPHE(state, action)
        is UpdateCatastrophe -> UPDATE_CATASTROPHE(state, action)
        // catastrophe
        is CreateLegalCode -> CREATE_LEGAL_CODE(state, action)
        is DeleteLegalCode -> DELETE_LEGAL_CODE(state, action)
        is UpdateLegalCode -> UPDATE_LEGAL_CODE(state, action)
        // realm
        is CreateRealm -> CREATE_REALM(state, action)
        is DeleteRealm -> DELETE_REALM(state, action)
        is UpdateRealm -> UPDATE_REALM(state, action)
        // town
        is CreateTown -> CREATE_TOWN(state, action)
        is DeleteTown -> DELETE_TOWN(state, action)
        is UpdateTown -> UPDATE_TOWN(state, action)
        // treaty
        is CreateTreaty -> CREATE_TREATY(state, action)
        is DeleteTreaty -> DELETE_TREATY(state, action)
        is UpdateTreaty -> UPDATE_TREATY(state, action)
        // war
        is CreateWar -> CREATE_WAR(state, action)
        is DeleteWar -> DELETE_WAR(state, action)
        is UpdateWar -> UPDATE_WAR(state, action)
    }
}
