package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // realm
        is CreateRealm -> CREATE_REALM(state, action)
        is DeleteRealm -> DELETE_REALM(state, action)
        is UpdateRealm -> UPDATE_REALM(state, action)
    }
}
