package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseStreetId(parameters: Parameters, param: String) = StreetId(parseInt(parameters, param))

fun parseStreet(id: StreetId, parameters: Parameters, state: State) = Street(
    id,
    parameters.getOrFail(NAME),
    parseDate(parameters, state, DATE),
)

