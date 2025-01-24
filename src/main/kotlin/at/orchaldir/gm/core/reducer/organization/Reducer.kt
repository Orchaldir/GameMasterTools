package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val ORGANIZATION_REDUCER: Reducer<OrganizationAction, State> = { state, action ->
    when (action) {
        is CreateOrganization -> CREATE_ORGANIZATION(state, action)
        is DeleteOrganization -> DELETE_ORGANIZATION(state, action)
        is UpdateOrganization -> UPDATE_ORGANIZATION(state, action)
    }
}
