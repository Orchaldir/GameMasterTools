package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.core.selector.religion.canDeleteGod
import at.orchaldir.gm.core.selector.religion.canDeletePantheon
import at.orchaldir.gm.utils.redux.Reducer

val RELIGION_REDUCER: Reducer<ReligionAction, State> = { state, action ->
    when (action) {
        // domain
        is CreateDomain -> CREATE_DOMAIN(state, action)
        is DeleteDomain -> deleteElement(state, action.id, State::canDeleteDomain)
        is UpdateDomain -> UPDATE_DOMAIN(state, action)
        // god
        is CreateGod -> CREATE_GOD(state, action)
        is DeleteGod -> deleteElement(state, action.id, State::canDeleteGod)
        is UpdateGod -> UPDATE_GOD(state, action)
        // god
        is CreatePantheon -> CREATE_PANTHEON(state, action)
        is DeletePantheon -> deleteElement(state, action.id, State::canDeletePantheon)
        is UpdatePantheon -> UPDATE_PANTHEON(state, action)
    }
}
