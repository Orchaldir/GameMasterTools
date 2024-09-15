package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseRiverId(parameters: Parameters, param: String) = RiverId(parseInt(parameters, param))

fun parseRiver(id: RiverId, parameters: Parameters) = River(
    id,
    parameters.getOrFail(NAME),
)
