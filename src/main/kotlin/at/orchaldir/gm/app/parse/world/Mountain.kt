package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.model.world.terrain.MountainId
import io.ktor.http.*

fun parseMountainId(parameters: Parameters, param: String) = MountainId(parseInt(parameters, param))

fun parseMountain(id: MountainId, parameters: Parameters) = Mountain(
    id,
    parseName(parameters),
    parameters.getAll(MATERIAL)?.map { MaterialId(it.toInt()) }?.toSet() ?: emptySet(),
)
