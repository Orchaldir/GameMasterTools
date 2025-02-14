package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val RELIGION_REDUCER: Reducer<ReligionAction, State> = { state, action ->
    when (action) {
        // god
        is CreateGod -> CREATE_GOD(state, action)
        is DeleteGod -> DELETE_GOD(state, action)
        is UpdateGod -> UPDATE_GOD(state, action)
    }
}
