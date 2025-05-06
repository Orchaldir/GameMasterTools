package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateUniform
import at.orchaldir.gm.core.action.DeleteUniform
import at.orchaldir.gm.core.action.UpdateUniform
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.selector.item.canDeleteUniform
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_UNIFORM: Reducer<CreateUniform, State> = { state, _ ->
    val uniform = Uniform(state.getUniformStorage().nextId)

    noFollowUps(state.updateStorage(state.getUniformStorage().add(uniform)))
}

val DELETE_UNIFORM: Reducer<DeleteUniform, State> = { state, action ->
    state.getUniformStorage().require(action.id)
    require(state.canDeleteUniform(action.id)) { "Uniform ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getUniformStorage().remove(action.id)))
}

val UPDATE_UNIFORM: Reducer<UpdateUniform, State> = { state, action ->
    val uniform = action.uniform

    validateUniform(state, uniform)

    noFollowUps(state.updateStorage(state.getUniformStorage().update(uniform)))
}

fun validateUniform(
    state: State,
    uniform: Uniform,
) {

}
