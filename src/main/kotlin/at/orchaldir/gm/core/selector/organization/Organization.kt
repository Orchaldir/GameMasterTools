package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.utils.Id

fun State.canDeleteOrganization(organization: OrganizationId) = !isCreator(organization)

fun <ID : Id<ID>> State.getOrganizationsFoundedBy(id: ID) = getOrganizationStorage()
    .getAll()
    .filter { it.founder.isId(id) }

fun State.getExistingOrganizations(date: Date?) = getExistingElements(getOrganizationStorage().getAll(), date)