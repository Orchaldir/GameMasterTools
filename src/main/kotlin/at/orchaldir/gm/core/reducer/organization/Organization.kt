package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.core.action.CreateOrganization
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.reducer.util.checkCreated
import at.orchaldir.gm.core.reducer.util.checkCreator
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ORGANIZATION: Reducer<CreateOrganization, State> = { state, _ ->
    val material = Organization(state.getOrganizationStorage().nextId)

    noFollowUps(state.updateStorage(state.getOrganizationStorage().add(material)))
}

val DELETE_ORGANIZATION: Reducer<DeleteOrganization, State> = { state, action ->
    state.getOrganizationStorage().require(action.id)

    checkCreated(state, action.id, "organization")

    noFollowUps(state.updateStorage(state.getOrganizationStorage().remove(action.id)))
}

val UPDATE_ORGANIZATION: Reducer<UpdateOrganization, State> = { state, action ->
    val organization = action.organization
    state.getOrganizationStorage().require(organization.id)

    checkCreator(state, organization.founder, organization.id, organization.date, "founder")

    noFollowUps(state.updateStorage(state.getOrganizationStorage().update(organization)))
}
