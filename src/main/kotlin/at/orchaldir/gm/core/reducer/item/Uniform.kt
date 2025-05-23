package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateUniform
import at.orchaldir.gm.core.action.DeleteUniform
import at.orchaldir.gm.core.action.UpdateUniform
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.item.canDeleteUniform
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_UNIFORM: Reducer<CreateUniform, State> = { state, _ ->
    val uniform = Uniform(state.getUniformStorage().nextId)

    noFollowUps(state.updateStorage(state.getUniformStorage().add(uniform)))
}

val DELETE_UNIFORM: Reducer<DeleteUniform, State> = { state, action ->
    state.getUniformStorage().require(action.id)
    validateCanDelete(state.canDeleteUniform(action.id), action.id)

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
    state.getUniformStorage().require(uniform.id)

    uniform.equipmentMap.getAllEquipment().forEach { pair ->
        state.getEquipmentStorage().require(pair.first)
        state.getColorSchemeStorage().require(pair.second)
    }
}
