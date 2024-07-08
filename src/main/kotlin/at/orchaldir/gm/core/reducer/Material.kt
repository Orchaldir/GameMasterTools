package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateMaterial
import at.orchaldir.gm.core.action.DeleteMaterial
import at.orchaldir.gm.core.action.UpdateMaterial
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_MATERIAL: Reducer<CreateMaterial, State> = { state, _ ->
    val material = Material(state.materials.nextId)

    noFollowUps(state.copy(materials = state.materials.add(material)))
}

val DELETE_MATERIAL: Reducer<DeleteMaterial, State> = { state, action ->
    state.materials.require(action.id)
    require(state.canDelete(action.id)) { "Material ${action.id.value} is used" }

    noFollowUps(state.copy(materials = state.materials.remove(action.id)))
}

val UPDATE_MATERIAL: Reducer<UpdateMaterial, State> = { state, action ->
    val material = action.material

    state.materials.require(material.id)

    noFollowUps(state.copy(materials = state.materials.update(material)))
}
