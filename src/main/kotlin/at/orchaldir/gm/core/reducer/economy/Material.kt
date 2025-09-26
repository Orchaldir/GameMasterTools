package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_MATERIAL: Reducer<UpdateMaterial, State> = { state, action ->
    val material = action.material

    state.getMaterialStorage().require(material.id)

    noFollowUps(state.updateStorage(state.getMaterialStorage().update(material)))
}
