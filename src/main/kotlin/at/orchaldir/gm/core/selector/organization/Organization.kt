package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.canDeleteOrganization(organization: OrganizationId) = true

// exists

fun State.exists(organization: Organization, date: Date?) = getDefaultCalendar()
    .isAfterOrEqualOptional(date, organization.date)

fun State.getExistingOrganization(date: Date?) = getOrganizationStorage()
    .getAll()
    .filter { exists(it, date) }
