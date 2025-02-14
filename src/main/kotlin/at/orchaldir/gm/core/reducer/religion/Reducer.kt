package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val RELIGION_REDUCER: Reducer<ReligionAction, State> = { state, action ->
    when (action) {
        // domain
        is CreateDomain -> CREATE_DOMAIN(state, action)
        is DeleteDomain -> DELETE_DOMAIN(state, action)
        is UpdateDomain -> UPDATE_DOMAIN(state, action)
        // god
        is CreateGod -> CREATE_GOD(state, action)
        is DeleteGod -> DELETE_GOD(state, action)
        is UpdateGod -> UPDATE_GOD(state, action)
    }
}
