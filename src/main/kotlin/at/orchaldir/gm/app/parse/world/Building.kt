package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.BuildingId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseBuildingId(parameters: Parameters, param: String) = BuildingId(parseInt(parameters, param))

fun parseUpdateBuilding(parameters: Parameters, state: State, id: BuildingId) = UpdateBuilding(
    id,
    parameters.getOrFail(NAME),
    parseDate(parameters, state, DATE),
)
