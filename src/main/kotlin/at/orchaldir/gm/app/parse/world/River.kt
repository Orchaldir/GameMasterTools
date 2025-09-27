package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import io.ktor.http.*

fun parseRiverId(parameters: Parameters, param: String) = RiverId(parseInt(parameters, param))

fun parseRiver(state: State, parameters: Parameters, id: RiverId) = River(
    id,
    parseName(parameters),
)
