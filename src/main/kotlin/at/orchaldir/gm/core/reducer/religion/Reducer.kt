package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.DeleteGod
import at.orchaldir.gm.core.action.DeletePantheon
import at.orchaldir.gm.core.action.ReligionAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.core.selector.religion.canDeleteGod
import at.orchaldir.gm.core.selector.religion.canDeletePantheon
import at.orchaldir.gm.utils.redux.Reducer

val RELIGION_REDUCER: Reducer<ReligionAction, State> = { state, action ->
    when (action) {
        // domain
        is DeleteDomain -> deleteElement(state, action.id, State::canDeleteDomain)
        // god
        is DeleteGod -> deleteElement(state, action.id, State::canDeleteGod)
        // god
        is DeletePantheon -> deleteElement(state, action.id, State::canDeletePantheon)
    }
}
