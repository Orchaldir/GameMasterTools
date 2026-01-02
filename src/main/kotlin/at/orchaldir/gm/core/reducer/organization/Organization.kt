package at.orchaldir.gm.core.reducer.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.reducer.util.validateHistory
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun validateRanks(state: State, organization: Organization) {
    require(organization.memberRanks.isNotEmpty()) { "Organization must have at least 1 rank!" }
}

fun validateMembers(state: State, organization: Organization) {
    organization.members.forEach { (characterId, history) ->
        val character = state.getCharacterStorage()
            .getOrThrow(characterId) { "Cannot use an unknown character ${characterId.value} as member!" }

        if (history.current == null) {
            require(history.previousEntries.isNotEmpty()) { "Member ${characterId.value} was never a member!" }
        }

        val birthdate = character.startDate(state)
        val startDate = state.getDefaultCalendar().max(birthdate, organization.date)

        validateHistory(state, history, startDate, "rank") { _, rank, noun, _ ->
            if (rank != null) {
                validateRank(organization, noun, rank)
            }
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
