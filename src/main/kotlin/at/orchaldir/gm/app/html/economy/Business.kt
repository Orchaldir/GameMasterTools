package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TEMPLATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.*
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.sortBusinessTemplates
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBusiness(
    call: ApplicationCall,
    state: State,
    business: Business,
) {
    val employees = state.getEmployees(business.id).toSet()
    val previousEmployees = state.getPreviousEmployees(business.id).toSet() - employees
    val startDate = business.startDate(state)

    fieldIds(call, state, business.templates)
    fieldPosition(call, state, business.position)
    optionalField(call, state, "Start", startDate)
    showVitalStatus(call, state, business.status)
    fieldAge("Age", state, startDate)
    fieldReference(call, state, business.founder, "Founder")
    showOwnership(call, state, business.ownership)
    showEmployees(call, state, employees, showOptionalBusiness = false)
    fieldElements(call, state, "Previous Employees", previousEmployees)
    showCreated(call, state, business.id)
    showOwnedElements(call, state, business.id)
    showDataSources(call, state, business.sources)
}

// edit

fun HtmlBlockTag.editBusiness(
    call: ApplicationCall,
    state: State,
    business: Business,
) {
    val startDate = business.startDate(state)

    selectName(business.name)
    selectElements(
        state,
        "Templates",
        TEMPLATE,
        state.sortBusinessTemplates(),
        business.templates,
    )
    selectOptionalDate(state, "Start", startDate, DATE)
    selectVitalStatus(
        state,
        business.id,
        startDate,
        business.status,
        ALLOWED_VITAL_STATUS_FOR_BUSINESS,
        ALLOWED_CAUSES_OF_DEATH_FOR_BUSINESS,
    )
    selectPosition(state, business.position, startDate, ALLOWED_BUSINESS_POSITIONS)
    selectCreator(state, business.founder, business.id, startDate, "Founder")
    selectOwnership(state, business.ownership, startDate)
    editDataSources(state, business.sources)
}

// parse

fun parseBusinessId(parameters: Parameters, param: String) = parseOptionalBusinessId(parameters, param) ?: BusinessId(0)
fun parseOptionalBusinessId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { BusinessId(it) }

fun parseBusiness(
    state: State,
    parameters: Parameters,
    id: BusinessId,
): Business {
    val startDate = parseOptionalDate(parameters, state, DATE)

    return Business(
        id,
        parseName(parameters),
        parseElements(parameters, TEMPLATE, ::parseBusinessTemplateId),
        startDate,
        parseVitalStatus(parameters, state),
        parseCreator(parameters),
        parseOwnership(parameters, state, startDate),
        parsePosition(parameters, state),
        parseDataSources(parameters),
    )
}
