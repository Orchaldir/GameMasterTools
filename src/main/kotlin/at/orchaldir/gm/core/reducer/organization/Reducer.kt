package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.core.action.CreateOrganization
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.OrganizationAction
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.organization.canDeleteOrganization
import at.orchaldir.gm.utils.redux.Reducer

val ORGANIZATION_REDUCER: Reducer<OrganizationAction, State> = { state, action ->
    when (action) {
        // organization
        is CreateOrganization -> CREATE_ORGANIZATION(state, action)
        is DeleteOrganization -> deleteElement(state, action.id, State::canDeleteOrganization)
        is UpdateOrganization -> UPDATE_ORGANIZATION(state, action)
    }
}
