package at.orchaldir.gm.app.html.model.organization

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.RANK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.MemberRank
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showOrganization(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    optionalField(call, state, "Date", organization.date)
    fieldCreator(call, state, organization.founder, "Founder")
    showCreated(call, state, organization.id)
    showRanks(organization)
}

private fun BODY.showRanks(organization: Organization) {
    showList("Ranks", organization.ranks) { rank ->
        field("Name", rank.name)
        field("Max Members", rank.maxNumber?.toString() ?: "Unlimited")
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
    editRanks(organization)
}

private fun FORM.editRanks(organization: Organization) {
    selectInt("Ranks", organization.ranks.size, 1, 20, 1, RANK, true)

    showListWithIndex(organization.ranks) { index, rank ->
        val rankParam = combine(RANK, index)
        selectName(rank.name, combine(rankParam, NAME))
        selectOptionalInt("Max Members", rank.maxNumber, 1, 100, 1, combine(rankParam, NUMBER))
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
    )

private fun parseRanks(parameters: Parameters): List<MemberRank> {
    val count = parseInt(parameters, RANK, 2)

    return (0..<count)
        .map { index ->
            val rankParam = combine(RANK, index)

            MemberRank(
                parameters[combine(rankParam, NAME)] ?: "Member",
                parseOptionalInt(parameters, combine(rankParam, NUMBER), 1),
            )
        }
}