package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.reducer.util.checkCreator
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.world.getBuilding
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS: Reducer<CreateBusiness, State> = { state, _ ->
    val material = Business(state.getBusinessStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessStorage().add(material)))
}

val DELETE_BUSINESS: Reducer<DeleteBusiness, State> = { state, action ->
    val business = state.getBusinessStorage().getOrThrow(action.id)
    require(state.getBuilding(action.id) == null) { "Cannot delete business ${action.id.value}, because it has a building!" }
    require(
        state.getBuildingsBuildBy(action.id).isEmpty()
    ) { "Cannot delete business ${action.id.value}, because it has build a building!" }
    require(state.getEmployees(action.id).isEmpty()) {
        "Cannot delete business ${action.id.value}, because it has employees!"
    }
    require(state.getPreviousEmployees(action.id).isEmpty()) {
        "Cannot delete business ${action.id.value}, because it has previous employees!"
    }
    checkCreator(state, business.founder, business.id, business.startDate, "Founder")

    noFollowUps(state.updateStorage(state.getBusinessStorage().remove(action.id)))
}

val UPDATE_BUSINESS: Reducer<UpdateBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.type.id)

    checkOwnership(state, action.type.ownership, action.type.startDate)

    noFollowUps(state.updateStorage(state.getBusinessStorage().update(action.type)))
}
