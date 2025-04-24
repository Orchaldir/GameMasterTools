package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.core.action.CreateOrganization
import at.orchaldir.gm.core.action.DeleteOrganization
import at.orchaldir.gm.core.action.UpdateOrganization
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkHistory
import at.orchaldir.gm.core.reducer.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ORGANIZATION: Reducer<CreateOrganization, State> = { state, _ ->
    val material = Organization(state.getOrganizationStorage().nextId)

    noFollowUps(state.updateStorage(state.getOrganizationStorage().add(material)))
}

val DELETE_ORGANIZATION: Reducer<DeleteOrganization, State> = { state, action ->
    state.getOrganizationStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id, "organization")
    checkIfOwnerCanBeDeleted(state, action.id)

    noFollowUps(state.updateStorage(state.getOrganizationStorage().remove(action.id)))
}

val UPDATE_ORGANIZATION: Reducer<UpdateOrganization, State> = { state, action ->
    val organization = action.organization
    state.getOrganizationStorage().require(organization.id)
    checkDate(state, organization.startDate(), "Organization")

    validateCreator(state, organization.founder, organization.id, organization.date, "founder")
    validateRanks(state, organization)
    validateMembers(state, organization)
    organization.holidays.forEach { state.getHolidayStorage().require(it) }

    noFollowUps(state.updateStorage(state.getOrganizationStorage().update(organization)))
}

private fun validateRanks(state: State, organization: Organization) {
    require(organization.memberRanks.isNotEmpty()) { "Organization must have at least 1 rank!" }
}

private fun validateMembers(state: State, organization: Organization) {
    organization.members.forEach { (characterId, history) ->
        val character = state.getCharacterStorage()
            .getOrThrow(characterId) { "Cannot use an unknown character ${characterId.value} as member!" }

        if (history.current == null) {
            require(history.previousEntries.isNotEmpty()) { "Member ${characterId.value} was never a member!" }
        }

        val startDate = state.getDefaultCalendar().max(character.birthDate, organization.date)
        var lastRank: Int? = -1

        checkHistory(state, history, startDate, "rank") { _, rank, noun, _ ->
            if (rank != null) {
                validateRank(organization, noun, rank)
            }
            require(rank != lastRank) { "The $noun is the same as the previous one for member ${characterId.value}!" }
            lastRank = rank
        }
    }
}

private fun validateRank(
    organization: Organization,
    noun: String,
    rank: Int,
) {
    require(rank < organization.memberRanks.size) { "Cannot use an unknown $noun $rank!" }
}
