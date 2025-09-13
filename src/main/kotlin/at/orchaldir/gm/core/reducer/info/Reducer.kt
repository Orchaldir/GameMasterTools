package at.orchaldir.gm.core.reducer.info

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.info.canDeleteObservation
import at.orchaldir.gm.utils.redux.Reducer

val INFORMATION_REDUCER: Reducer<InformationAction, State> = { state, action ->
    when (action) {
        // observation
        is CreateObservation -> CREATE_OBSERVATION(state, action)
        is DeleteObservation -> deleteElement(state, action.id, State::canDeleteObservation)
        is UpdateObservation -> UPDATE_OBSERVATION(state, action)
    }
}
