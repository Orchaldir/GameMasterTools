package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateMaterial
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.canDeleteMaterial
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MATERIAL: Reducer<CreateMaterial, State> = { state, _ ->
    val material = Material(state.getMaterialStorage().nextId)

    noFollowUps(state.updateStorage(state.getMaterialStorage().add(material)))
}

val DELETE_MATERIAL: Reducer<DeleteMaterial, State> = { state, action ->
    state.getMaterialStorage().require(action.id)
    validateCanDelete(state.canDeleteMaterial(action.id), action.id)

    noFollowUps(state.updateStorage(state.getMaterialStorage().remove(action.id)))
}

val UPDATE_MATERIAL: Reducer<UpdateMaterial, State> = { state, action ->
    val material = action.material

    state.getMaterialStorage().require(material.id)

    noFollowUps(state.updateStorage(state.getMaterialStorage().update(material)))
}
