package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseMaterialCost
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseStreetTypeId(parameters: Parameters, param: String) = StreetTypeId(parseInt(parameters, param))

fun parseStreetType(id: StreetTypeId, parameters: Parameters) = StreetType(
    id,
    parameters.getOrFail(NAME),
    parse(parameters, COLOR, Color.SkyBlue),
    parseMaterialCost(parameters),
)

