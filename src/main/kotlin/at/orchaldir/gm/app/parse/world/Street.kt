package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import io.ktor.http.*

fun parseStreetId(parameters: Parameters, param: String) = StreetId(parseInt(parameters, param))

fun parseStreet(id: StreetId, parameters: Parameters) = Street(
    id,
    parseName(parameters),
)

