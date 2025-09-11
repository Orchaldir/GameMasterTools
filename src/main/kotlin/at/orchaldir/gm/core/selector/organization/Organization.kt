package at.orchaldir.gm.core.selector.organization

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.selector.util.canDeleteCreator
import at.orchaldir.gm.core.selector.util.canDeleteDestroyer
import at.orchaldir.gm.core.selector.util.canDeleteOwner
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteOrganization(organization: OrganizationId) = DeleteResult(organization)
    .apply { canDeleteCreator(organization, it) }
    .apply { canDeleteDestroyer(organization, it) }
    .apply { canDeleteOwner(organization, it) }

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
