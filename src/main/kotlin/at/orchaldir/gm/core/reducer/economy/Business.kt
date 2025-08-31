package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.ALLOWED_BUSINESS_POSITIONS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS: Reducer<CreateBusiness, State> = { state, _ ->
    val material = Business(state.getBusinessStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessStorage().add(material)))
}

val DELETE_BUSINESS: Reducer<DeleteBusiness, State> = { state, action ->
    state.getBusinessStorage().require(action.id)
    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)
    validateCanDelete(state.getEmployees(action.id).isEmpty(), action.id, "it has employees")
    validateCanDelete(state.getPreviousEmployees(action.id).isEmpty(), action.id, "it has previous employees")

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
    checkDate(state, business.startDate(), "Business Founding")
    validateCreator(state, business.founder, business.id, business.startDate(), "Founder")
    checkPosition(state, business.position, "position", business.startDate(), ALLOWED_BUSINESS_POSITIONS)
    checkOwnership(state, business.ownership, business.startDate())
}
