package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.fieldAge
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.item.getTextsPublishedBy
import at.orchaldir.gm.core.selector.world.getBuilding
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBusiness(
    call: ApplicationCall,
    state: State,
    business: Business,
) {

    val employees = state.getEmployees(business.id).toSet()
    val previousEmployees = state.getPreviousEmployees(business.id).toSet() - employees
    val published = state.getTextsPublishedBy(business.id)

    fieldReferenceByName(call, state, business.name)
    state.getBuilding(business.id)?.let { fieldLink("Building", call, state, it) }
    optionalField(call, state, "Start", business.startDate())
    fieldAge("Age", state, business.startDate())
    fieldCreator(call, state, business.founder, "Founder")
    showOwnership(call, state, business.ownership)
    showEmployees(call, state, "Employees", employees)
    showList("Previous Employees", previousEmployees) { character ->
        link(call, state, character)
    }
    showCreated(call, state, business.id, true)

    showList("Published Texts", published) { text ->
        link(call, state, text)
    }

    showOwnedElements(call, state, business.id, true)
}

// edit

fun FORM.editBusiness(
    state: State,
    business: Business,
) {
    selectComplexName(state, business.name)
    selectOptionalDate(state, "Start", business.startDate(), DATE)
    selectCreator(state, business.founder, business.id, business.startDate(), "Founder")
    selectOwnership(state, business.ownership, business.startDate())
}

// parse

fun parseBusinessId(parameters: Parameters, param: String) = parseOptionalBusinessId(parameters, param) ?: BusinessId(0)
fun parseOptionalBusinessId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { BusinessId(it) }

fun parseBusiness(parameters: Parameters, state: State, id: BusinessId): Business {
    val name = parseComplexName(parameters)
    val startDate = parseOptionalDate(parameters, state, DATE)

    return Business(
        id,
        name,
        startDate,
        parseCreator(parameters),
        parseOwnership(parameters, state, startDate),
    )
}
