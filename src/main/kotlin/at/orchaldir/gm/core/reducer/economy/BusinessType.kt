package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusinessType
import at.orchaldir.gm.core.action.DeleteBusinessType
import at.orchaldir.gm.core.action.UpdateBusinessType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessType
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS_TYPE: Reducer<CreateBusinessType, State> = { state, _ ->
    val material = BusinessType(state.getBusinessTypeStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessTypeStorage().add(material)))
}

val DELETE_BUSINESS_TYPE: Reducer<DeleteBusinessType, State> = { state, action ->
    state.getBusinessTypeStorage().require(action.id)
    require(state.canDelete(action.id)) { "Business Type ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getBusinessTypeStorage().remove(action.id)))
}

val UPDATE_BUSINESS_TYPE: Reducer<UpdateBusinessType, State> = { state, action ->
    state.getBusinessTypeStorage().require(action.type.id)

    noFollowUps(state.updateStorage(state.getBusinessTypeStorage().update(action.type)))
}
