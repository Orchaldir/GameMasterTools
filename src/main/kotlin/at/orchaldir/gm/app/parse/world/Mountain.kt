package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import io.ktor.http.*

fun parseMountainId(parameters: Parameters, param: String) = RegionId(parseInt(parameters, param))

fun parseMountain(id: RegionId, parameters: Parameters) = Region(
    id,
    parseName(parameters),
    parameters.getAll(MATERIAL)?.map { MaterialId(it.toInt()) }?.toSet() ?: emptySet(),
)
