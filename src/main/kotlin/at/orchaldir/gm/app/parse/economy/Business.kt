package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBusinessId(parameters: Parameters, param: String) = BusinessId(parseInt(parameters, param))

fun parseBusiness(id: BusinessId, parameters: Parameters): Business {
    val name = parameters.getOrFail(NAME)

    return Business(id, name)
}
