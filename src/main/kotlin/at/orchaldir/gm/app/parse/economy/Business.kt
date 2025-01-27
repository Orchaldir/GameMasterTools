package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.model.parseComplexName
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.parseOwnership
import at.orchaldir.gm.app.parse.parseOptionalIdValue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import io.ktor.http.*

fun parseBusinessId(parameters: Parameters, param: String) = parseOptionalBusinessId(parameters, param) ?: BusinessId(0)
fun parseOptionalBusinessId(parameters: Parameters, param: String) =
    parseOptionalIdValue(parameters, param)?.let { BusinessId(it) }

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
