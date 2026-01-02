package at.orchaldir.gm.app.html.organization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseOptionalCharacterId
import at.orchaldir.gm.app.html.time.editHolidays
import at.orchaldir.gm.app.html.time.parseHolidays
import at.orchaldir.gm.app.html.time.showHolidays
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.*
import at.orchaldir.gm.core.selector.organization.getNotMembers
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showOrganization(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    optionalField(call, state, "Date", organization.date)
    fieldReference(call, state, organization.founder, "Founder")
    showVitalStatus(call, state, organization.status)
    showMembers(call, state, organization)
    showBeliefStatusHistory(call, state, organization.beliefStatus)
    showDataSources(call, state, organization.sources)
    showCreated(call, state, organization.id)
    showHolidays(call, state, organization.holidays)
    showOwnedElements(call, state, organization.id)
}


private fun HtmlBlockTag.showMembers(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    h2 { +"Members" }

    showListWithIndex(organization.memberRanks) { index, rank ->
        fieldName("Rank", rank.name)
        fieldIds(call, state, "Members", organization.getMembers(index))
    }
}

// edit

fun HtmlBlockTag.editOrganization(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    selectName(organization.name)
    selectOptionalDate(state, "Date", organization.date, DATE)
    selectCreator(state, organization.founder, organization.id, organization.date, "Founder")
    selectVitalStatus(
        state,
        organization.id,
        organization.date,
        organization.status,
        ALLOWED_VITAL_STATUS_FOR_ORGANIZATION,
        ALLOWED_CAUSES_OF_DEATH_FOR_ORGANIZATION,
    )
    editMembers(state, organization)
    editBeliefStatusHistory(state, organization.beliefStatus, organization.date)
    editHolidays(state, organization.holidays)
    editDataSources(state, organization.sources)
}

private fun HtmlBlockTag.editMembers(
    state: State,
    organization: Organization,
) {
    val notMembers = state.getNotMembers(organization)
    val maxMembers = organization.members.size + notMembers.size
    val rankIds = (0..<organization.memberRanks.size).toSet()

    h2 { +"Members" }

    editList(
        "Ranks",
        RANK,
        organization.memberRanks,
        1,
        20,
    ) { _, param, rank ->
        selectName("Name", rank.name, combine(param, NAME))
    }
    editMap(
        "Members",
        MEMBER,
        organization.members,
        0,
        maxMembers,
    ) { _, memberParam, characterId, history ->
        val character = state.getCharacterStorage().getOrThrow(characterId)
        val potentialCharacters = state.getCharacterStorage().get(notMembers + characterId)

        selectElement(
            state,
            combine(memberParam, CHARACTER),
            potentialCharacters,
            characterId,
        )
        selectHistory(
            state,
            combine(memberParam, RANK),
            history,
            "Rank",
            character.startDate(state)
        ) { _, param, currentRank, _ ->
            selectOptionalValue("Rank", param, currentRank, rankIds) { rank ->
                label = organization.memberRanks[rank].name.text
                value = rank.toString()
            }
        }
    }
}

// parse

fun parseOrganizationId(parameters: Parameters, param: String) = OrganizationId(parseInt(parameters, param))

fun parseOrganization(
    state: State,
    parameters: Parameters,
    id: OrganizationId,
): Organization {
    val date = parseOptionalDate(parameters, state, DATE)

    return Organization(
        id,
        parseName(parameters),
        parseCreator(parameters),
        date,
        parseVitalStatus(parameters, state),
        parseList(parameters, RANK, 1) { index, param ->
            parseRank(parameters, index, param)
        },
        parseMembers(state, parameters, id),
        parseBeliefStatusHistory(parameters, state, date),
        parseHolidays(parameters),
        parseDataSources(parameters),
    )
}

private fun parseRank(parameters: Parameters, index: Int, param: String) = MemberRank(
    parseName(parameters, combine(param, NAME), "${index + 1}.Rank"),
)

private fun parseMembers(
    state: State,
    parameters: Parameters,
    id: OrganizationId,
) = parseIdMap(
    parameters,
    MEMBER,
    state.getNotMembers(id).toList(),
    { _, keyParam ->
        parseOptionalCharacterId(parameters, combine(keyParam, CHARACTER))
    },
) { characterId, _, memberParam ->
    val character = state.getCharacterStorage().getOrThrow(characterId)
    parseHistory(
        parameters,
        combine(memberParam, RANK),
        state,
        character.startDate(state),
        ::parseMemberRank,
    )
}

fun parseMemberRank(parameters: Parameters, state: State, param: String) = parseSimpleOptionalInt(parameters, param)
