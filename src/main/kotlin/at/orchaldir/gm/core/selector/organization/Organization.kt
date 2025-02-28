package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.organization.Organization
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

fun State.getOrganizations(member: CharacterId) = getOrganizationStorage()
    .getAll()
    .filter { it.members.containsKey(member) }

fun State.getOrganizations(holiday: HolidayId) = getOrganizationStorage()
    .getAll()
    .filter { it.holidays.contains(holiday) }

fun State.getNotMembers(organization: OrganizationId) = getNotMembers(getOrganizationStorage().getOrThrow(organization))

fun State.getNotMembers(organization: Organization): Set<CharacterId> {
    val members = organization.getAllMembers()

    return getCharacterStorage().getIds() - members
}
