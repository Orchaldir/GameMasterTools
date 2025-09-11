package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.CreatePantheon
import at.orchaldir.gm.core.action.UpdatePantheon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PANTHEON: Reducer<CreatePantheon, State> = { state, _ ->
    val pantheon = Pantheon(state.getPantheonStorage().nextId)

    noFollowUps(state.updateStorage(state.getPantheonStorage().add(pantheon)))
}

val UPDATE_PANTHEON: Reducer<UpdatePantheon, State> = { state, action ->
    val pantheon = action.pantheon
    state.getPantheonStorage().require(pantheon.id)
    validatePantheon(state, pantheon)

    noFollowUps(state.updateStorage(state.getPantheonStorage().update(pantheon)))
}

fun validatePantheon(state: State, pantheon: Pantheon) {
    pantheon.gods.forEach { state.getGodStorage().require(it) }
}
