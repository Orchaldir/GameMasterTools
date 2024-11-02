package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS: Reducer<CreateBusiness, State> = { state, _ ->
    val material = Business(state.getBusinessStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessStorage().add(material)))
}

val DELETE_BUSINESS: Reducer<DeleteBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.id)
    require(state.canDelete(action.id)) { "Business ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getBusinessStorage().remove(action.id)))
}

val UPDATE_BUSINESS: Reducer<UpdateBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.type.id)

    checkOwnership(state, action.type.ownership, action.type.startDate)

    noFollowUps(state.updateStorage(state.getBusinessStorage().update(action.type)))
}
