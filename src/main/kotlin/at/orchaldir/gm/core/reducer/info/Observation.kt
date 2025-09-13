package at.orchaldir.gm.core.reducer.info

import at.orchaldir.gm.core.action.CreateObservation
import at.orchaldir.gm.core.action.UpdateObservation
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.Observation
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_OBSERVATION: Reducer<CreateObservation, State> = { state, _ ->
    val observation = Observation(state.getObservationStorage().nextId)

    noFollowUps(state.updateStorage(state.getObservationStorage().add(observation)))
}

val UPDATE_OBSERVATION: Reducer<UpdateObservation, State> = { state, action ->
    val observation = action.observation
    state.getObservationStorage().require(observation.id)

    validateObservation(state, observation)

    noFollowUps(state.updateStorage(state.getObservationStorage().update(observation)))
}

fun validateObservation(state: State, observation: Observation) {

}
