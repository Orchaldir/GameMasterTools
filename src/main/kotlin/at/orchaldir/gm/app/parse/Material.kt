package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseMaterialId(parameters: Parameters, param: String) = MaterialId(parseInt(parameters, param))

fun parseMaterial(id: MaterialId, parameters: Parameters): Material {
    val name = parameters.getOrFail(NAME)

    return Material(id, name)
}
