package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseFill
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseStreetTypeId(parameters: Parameters, param: String) = StreetTypeId(parseInt(parameters, param))

fun parseStreetType(id: StreetTypeId, parameters: Parameters) = StreetType(
    id,
    parameters.getOrFail(NAME),
    parseFill(parameters),
)
