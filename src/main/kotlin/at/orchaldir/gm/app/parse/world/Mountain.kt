package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.model.world.terrain.MountainId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseMountainId(parameters: Parameters, param: String) = MountainId(parseInt(parameters, param))

fun parseMountain(id: MountainId, parameters: Parameters) = Mountain(
    id,
    parameters.getOrFail(NAME),
)
