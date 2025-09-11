package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.ALLOWED_BUSINESS_POSITIONS
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_BUSINESS: Reducer<CreateBusiness, State> = { state, _ ->
    val material = Business(state.getBusinessStorage().nextId)

    noFollowUps(state.updateStorage(state.getBusinessStorage().add(material)))
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
