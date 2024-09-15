package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.util.*

fun parseMoonId(parameters: Parameters, param: String) = MoonId(parseInt(parameters, param))

fun parseMoon(id: MoonId, parameters: Parameters) = Moon(
    id,
    parameters.getOrFail(NAME),
    parseInt(parameters, LENGTH, 1),
    parse(parameters, COLOR, Color.White),
)
