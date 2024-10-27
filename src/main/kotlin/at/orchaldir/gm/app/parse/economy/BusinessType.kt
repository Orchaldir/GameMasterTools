package at.orchaldir.gm.app.parse.economy

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.economy.business.BusinessType
import at.orchaldir.gm.core.model.economy.business.BusinessTypeId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBusinessTypeId(parameters: Parameters, param: String) = BusinessTypeId(parseInt(parameters, param))

fun parseBusinessType(id: BusinessTypeId, parameters: Parameters): BusinessType {
    val name = parameters.getOrFail(NAME)

    return BusinessType(id, name)
}
