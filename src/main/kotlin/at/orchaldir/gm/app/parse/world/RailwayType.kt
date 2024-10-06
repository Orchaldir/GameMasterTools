package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.railway.RailwayType
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRailwayTypeId(parameters: Parameters, param: String) = RailwayTypeId(parseInt(parameters, param))

fun parseRailwayType(id: RailwayTypeId, parameters: Parameters) = RailwayType(
    id,
    parameters.getOrFail(NAME),
)

