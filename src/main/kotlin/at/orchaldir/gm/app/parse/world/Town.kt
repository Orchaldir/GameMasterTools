package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))

fun parseTown(oldTown: Town, parameters: Parameters) = oldTown.copy(name = parameters.getOrFail(NAME))
