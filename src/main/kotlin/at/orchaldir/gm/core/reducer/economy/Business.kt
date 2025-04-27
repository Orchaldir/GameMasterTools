package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.reducer.util.checkComplexName
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOwnershipWithOptionalDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.world.getBuilding
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS: Reducer<CreateBusiness, State> = { state, _ ->
    val material = Business(state.getBusinessStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessStorage().add(material)))
}

val DELETE_BUSINESS: Reducer<DeleteBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.id)
    require(state.getBuilding(action.id) == null) { "Cannot delete business ${action.id.value}, because it has a building!" }
    checkIfCreatorCanBeDeleted(state, action.id)
    require(state.getEmployees(action.id).isEmpty()) {
        "Cannot delete business ${action.id.value}, because it has employees!"
    }
    require(state.getPreviousEmployees(action.id).isEmpty()) {
        "Cannot delete business ${action.id.value}, because it has previous employees!"
    }

    noFollowUps(state.updateStorage(state.getBusinessStorage().remove(action.id)))
}

val UPDATE_BUSINESS: Reducer<UpdateBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.business.id)
    val business = action.business

    validateBusiness(state, business)

    noFollowUps(state.updateStorage(state.getBusinessStorage().update(action.business)))
}

fun validateBusiness(
    state: State,
    business: Business,
) {
    checkComplexName(state, business.name)
    checkDate(state, business.startDate(), "Business Founding")
    validateCreator(state, business.founder, business.id, business.startDate(), "Founder")
    checkOwnershipWithOptionalDate(state, business.ownership, business.startDate())
}
