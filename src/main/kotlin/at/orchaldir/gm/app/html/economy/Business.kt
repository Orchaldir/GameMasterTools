package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editDataSources
import at.orchaldir.gm.app.html.util.fieldAge
import at.orchaldir.gm.app.html.util.fieldCreator
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseCreator
import at.orchaldir.gm.app.html.util.parseDataSources
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.parseOwnership
import at.orchaldir.gm.app.html.util.selectCreator
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.selectOwnership
import at.orchaldir.gm.app.html.util.showCreated
import at.orchaldir.gm.app.html.util.showDataSources
import at.orchaldir.gm.app.html.util.showEmployees
import at.orchaldir.gm.app.html.util.showOwnedElements
import at.orchaldir.gm.app.html.util.showOwnership
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
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

    state.getBuilding(business.id)?.let { fieldLink("Building", call, state, it) }
    optionalField(call, state, "Start", business.startDate())
    fieldAge("Age", state, business.startDate())
    fieldCreator(call, state, business.founder, "Founder")
    showOwnership(call, state, business.ownership)
    showEmployees(call, state, employees, showOptionalBusiness = false)
    fieldList(call, state, "Previous Employees", previousEmployees)
    showCreated(call, state, business.id, true)

    fieldList(call, state, "Published Texts", published)

    showOwnedElements(call, state, business.id, true)
    showDataSources(call, state, business.sources)
}

// edit

fun FORM.editBusiness(
    state: State,
    business: Business,
) {
    selectName(business.name)
    selectOptionalDate(state, "Start", business.startDate(), DATE)
    selectCreator(state, business.founder, business.id, business.startDate(), "Founder")
    selectOwnership(state, business.ownership, business.startDate())
    editDataSources(state, business.sources)
}

// parse

fun parseBusinessId(parameters: Parameters, param: String) = parseOptionalBusinessId(parameters, param) ?: BusinessId(0)
fun parseOptionalBusinessId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { BusinessId(it) }

fun parseBusiness(parameters: Parameters, state: State, id: BusinessId): Business {
    val startDate = parseOptionalDate(parameters, state, DATE)

    return Business(
        id,
        parseName(parameters),
        startDate,
        parseCreator(parameters),
        parseOwnership(parameters, state, startDate),
        parseDataSources(parameters),
    )
}
