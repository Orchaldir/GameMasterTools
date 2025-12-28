package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.TEMPLATE
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.fieldIds
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectElements
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.*
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.item.getTextsPublishedBy
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
    val published = state.getTextsPublishedBy(business.id)

    fieldIds(call, state, business.templates)
    fieldPosition(call, state, business.position)
    optionalField(call, state, "Start", business.startDate())
    showVitalStatus(call, state, business.status)
    fieldAge("Age", state, business.startDate())
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
    selectName(business.name)
    selectElements(
        state,
        "Templates",
        TEMPLATE,
        state.sortBusinessTemplates(),
        business.templates,
    )
    selectOptionalDate(state, "Start", business.startDate(), DATE)
    selectVitalStatus(
        state,
        business.id,
        business.startDate(),
        business.status,
        ALLOWED_VITAL_STATUS_FOR_BUSINESS,
        ALLOWED_CAUSES_OF_DEATH_FOR_BUSINESS,
    )
    selectPosition(state, POSITION, business.position, business.startDate(), ALLOWED_BUSINESS_POSITIONS)
    selectCreator(state, business.founder, business.id, business.startDate(), "Founder")
    selectOwnership(state, business.ownership, business.startDate())
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
