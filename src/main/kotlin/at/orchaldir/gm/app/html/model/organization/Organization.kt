package at.orchaldir.gm.app.html.model.organization

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.MemberRank
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
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

// edit

fun FORM.editOrganization(
    call: ApplicationCall,
    state: State,
    organization: Organization,
) {
    selectName(organization.name)
    selectOptionalDate(state, "Date", organization.date, DATE)
    selectCreator(state, organization.founder, organization.id, organization.date, "Founder")
}

// parse

fun parseOrganizationId(parameters: Parameters, param: String) = OrganizationId(parseInt(parameters, param))

fun parseOrganization(parameters: Parameters, state: State, id: OrganizationId) =
    Organization(
        id,
        parameters.getOrFail(NAME),
        parseCreator(parameters),
        parseOptionalDate(parameters, state, DATE),
        parseMemberRanks(parameters),
    )

fun parseMemberRanks(parameters: Parameters): List<MemberRank> = emptyList()
