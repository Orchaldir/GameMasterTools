package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.core.model.world.region.River
import at.orchaldir.gm.core.model.world.region.RiverId
import io.ktor.http.*

fun parseRiverId(parameters: Parameters, param: String) = RiverId(parseInt(parameters, param))

fun parseRiver(id: RiverId, parameters: Parameters) = River(
    id,
    parseName(parameters),
)
