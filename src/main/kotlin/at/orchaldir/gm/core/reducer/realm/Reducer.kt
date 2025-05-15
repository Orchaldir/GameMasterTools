package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateRealm
import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.RealmAction
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // realm
        is CreateRealm -> CREATE_REALM(state, action)
        is DeleteRealm -> DELETE_REALM(state, action)
        is UpdateRealm -> UPDATE_REALM(state, action)
        // town
        is CreateTown -> CREATE_TOWN(state, action)
        is DeleteTown -> DELETE_TOWN(state, action)
        is UpdateTown -> UPDATE_TOWN(state, action)
    }
}
