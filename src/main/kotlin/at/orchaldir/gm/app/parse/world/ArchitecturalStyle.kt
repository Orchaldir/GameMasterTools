package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseArchitecturalStyleId(parameters: Parameters, param: String) = ArchitecturalStyleId(parseInt(parameters, param))

fun parseArchitecturalStyle(id: ArchitecturalStyleId, parameters: Parameters) = ArchitecturalStyle(
    id,
    parameters.getOrFail(NAME),
)
