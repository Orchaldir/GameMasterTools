package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.moon.Moon
import at.orchaldir.gm.core.model.moon.MoonId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseMoonId(parameters: Parameters, param: String) = MoonId(parseInt(parameters, param))

fun parseMoon(id: MoonId, parameters: Parameters): Moon {
    val name = parameters.getOrFail(NAME)

    return Moon(id, name)
}
