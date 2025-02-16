package at.orchaldir.gm.app.html.model.world

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.optionalField
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlane(
    plane: Plane,
) {
    optionalField("Title", plane.title)
}

// edit

fun HtmlBlockTag.editPlane(
    plane: Plane,
) {
    selectName(plane.name)
    selectText("Optional Name", plane.title ?: "", TILE, 0)
}

// parse

fun parsePlaneId(parameters: Parameters, param: String) = PlaneId(parseInt(parameters, param))

fun parsePlaneId(value: String) = PlaneId(value.toInt())

fun parsePlane(parameters: Parameters, id: PlaneId) = Plane(
    id,
    parameters.getOrFail(NAME),
    parameters[TILE]?.ifEmpty { null },
)
