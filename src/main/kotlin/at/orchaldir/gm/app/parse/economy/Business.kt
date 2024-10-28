package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseDate
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.app.parse.parseOwnership
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBusinessId(parameters: Parameters, param: String) = parseOptionalInt(parameters, param)?.let { BusinessId(it) }

fun parseBusiness(parameters: Parameters, state: State, id: BusinessId): Business {
    val name = parameters.getOrFail(NAME)
    val startDate = parseDate(parameters, state, DATE)

    return Business(
        id,
        name,
        startDate,
        parseOwnership(parameters, state, startDate),
    )
}
