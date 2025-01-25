package at.orchaldir.gm.app.parse.organization

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.organization.OrganizationId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseOrganizationId(parameters: Parameters, param: String) = OrganizationId(parseInt(parameters, param))

fun parseOrganization(parameters: Parameters, state: State, id: OrganizationId) =
    Organization(
        id,
        parameters.getOrFail(NAME),
        parseCreator(parameters),
        parseOptionalDate(parameters, state, DATE)
    )
