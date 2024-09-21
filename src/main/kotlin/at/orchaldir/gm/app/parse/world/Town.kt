package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))

fun parseTown(parameters: Parameters, state: State, oldTown: Town) = oldTown.copy(
    name = parameters.getOrFail(NAME),
    foundingDate = parseDate(parameters, state, DATE),
)
