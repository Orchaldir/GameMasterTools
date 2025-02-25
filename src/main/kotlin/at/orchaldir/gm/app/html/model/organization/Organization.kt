package at.orchaldir.gm.app.html.model.organization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.time.editHolidays
import at.orchaldir.gm.app.html.model.time.parseHolidays
import at.orchaldir.gm.app.html.model.time.showHolidays
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.organization.MemberRank
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.organization.getNotMembers
import at.orchaldir.gm.core.selector.util.sortCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showOrganization(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    optionalField(call, state, "Date", organization.date)
    fieldCreator(call, state, organization.founder, "Founder")
    showCreated(call, state, organization.id)
    showMembers(call, state, organization)
    showPossession(call, state, organization)
    showHolidays(call, state, organization.holidays)
}

private fun HtmlBlockTag.showPossession(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    h2 { +"Possession" }

    showOwnedElements(call, state, organization.id)
}

private fun HtmlBlockTag.showMembers(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    h2 { +"Members" }

    showListWithIndex(organization.memberRanks) { index, rank ->
        field("Rank", rank.name)
        showList("Members", organization.getMembers(index)) { character ->
            link(call, state, character)
        }
    }
}

// edit

fun FORM.editOrganization(
    state: State,
    organization: Organization,
) {
    selectName(organization.name)
    selectOptionalDate(state, "Date", organization.date, DATE)
    selectCreator(state, organization.founder, organization.id, organization.date, "Founder")
    editMembers(state, organization)
    editHolidays(state, organization.holidays)
}

private fun FORM.editMembers(
    state: State,
    organization: Organization,
) {
    val notMembers = state.getNotMembers(organization)
    val maxMembers = organization.members.size + notMembers.size
    val rankIds = (0..<organization.memberRanks.size).toSet()

    h2 { +"Members" }

    selectInt("Ranks", organization.memberRanks.size, 1, 20, 1, RANK, true)
    showListWithIndex(organization.memberRanks) { index, rank ->
        selectText("Name", rank.name, combine(RANK, NAME, index), 1)
    }
    selectInt("Members", organization.members.size, 0, maxMembers, 1, MEMBER, true)
    showListWithIndex(organization.members.entries) { memberIndex, (characterId, history) ->
        val character = state.getCharacterStorage().getOrThrow(characterId)
        val potentialCharacters = state.sortCharacters(state.getCharacterStorage().get(notMembers + characterId))
        val memberParam = combine(MEMBER, memberIndex)

        selectElement("Character", combine(memberParam, CHARACTER), potentialCharacters, characterId, true)
        selectHistory(
            state,
            combine(memberParam, RANK),
            history,
            character.birthDate,
            "Rank"
        ) { _, param, currentRank, _ ->
            selectOptionalValue("Rank", param, currentRank, rankIds) { rank ->
                label = organization.memberRanks[rank].name
                value = rank.toString()
            }
        }
    }
}

// parse

fun parseOrganizationId(parameters: Parameters, param: String) = OrganizationId(parseInt(parameters, param))

fun parseOrganization(parameters: Parameters, state: State, id: OrganizationId) =
    Organization(
        id,
        parameters.getOrFail(NAME),
        parseCreator(parameters),
        parseOptionalDate(parameters, state, DATE),
        parseRanks(parameters),
        parseMembers(state, parameters, id),
        parseHolidays(parameters),
    )

private fun parseRanks(parameters: Parameters): List<MemberRank> {
    val count = parseInt(parameters, RANK, 1)

    return (0..<count)
        .map { parseRank(parameters, it) }
}

private fun parseRank(parameters: Parameters, index: Int) = MemberRank(
    parseOptionalString(parameters, combine(RANK, NAME, index)) ?: "${index + 1}.Rank",
)

private fun parseMembers(
    state: State,
    parameters: Parameters,
    id: OrganizationId,
): Map<CharacterId, History<Int?>> {
    val count = parseInt(parameters, MEMBER, 0)
    val members = mutableMapOf<CharacterId, History<Int?>>()
    val notMembers = state.getNotMembers(id).toList()
    var notMemberIndex = 0

    for (memberIndex in 0..<count) {
        val memberParam = combine(MEMBER, memberIndex)
        val characterId =
            parseOptionalCharacterId(parameters, combine(memberParam, CHARACTER)) ?: notMembers[notMemberIndex++]
        val character = state.getCharacterStorage().getOrThrow(characterId)
        val history =
            parseHistory(parameters, combine(memberParam, RANK), state, character.birthDate, ::parseMemberRank)

        members[characterId] = history
    }

    return members
}

fun parseMemberRank(parameters: Parameters, state: State, param: String) = parseOptionalInt(parameters, param)
