package at.orchaldir.gm.app.html.model.organization

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.MemberRank
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.selector.organization.getPotentialMembers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.*

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

    showList(organization.memberRanks) { rank ->
        field("Rank", rank.name)
        showList("Members", rank.members) { character ->
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
}

private fun FORM.editMembers(
    state: State,
    organization: Organization,
) {

    h2 { +"Members" }

    selectInt("Ranks", organization.memberRanks.size, 1, 20, 1, RANK, true)


    showListWithIndex(organization.memberRanks) { index, rank ->
        val characters = state.getPotentialMembers(organization, index)

        selectText("Name", rank.name, combine(RANK, NAME, index), 1)
        selectElements(state, "Members", combine(RANK, CHARACTER, index), characters, rank.members)
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
        parseMembers(parameters),
    )

fun parseMembers(parameters: Parameters): List<MemberRank> {
    val count = parseInt(parameters, LIFE_STAGE, 2)

    return (0..<count)
        .map { parseMemberRank(parameters, it) }
}

private fun parseMemberRank(parameters: Parameters, index: Int) = MemberRank(
    parseOptionalString(parameters, combine(RANK, NAME, index)) ?: "${index + 1}.Rank",
    parseElements(parameters, combine(RANK, CHARACTER, index), ::parseCharacterId),
)
