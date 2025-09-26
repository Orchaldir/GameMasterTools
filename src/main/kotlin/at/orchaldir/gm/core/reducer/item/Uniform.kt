package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.UpdateUniform
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps


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
