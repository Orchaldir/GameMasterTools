package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.REVIVAL
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.model.parseOptionalYear
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import io.ktor.http.*

fun parseArchitecturalStyleId(parameters: Parameters, param: String) = ArchitecturalStyleId(parseInt(parameters, param))

fun parseOptionalArchitecturalStyleId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { ArchitecturalStyleId(it) }

fun parseArchitecturalStyle(parameters: Parameters, state: State, id: ArchitecturalStyleId) = ArchitecturalStyle(
    id,
    parseName(parameters),
    parseOptionalYear(parameters, state, START),
    parseOptionalYear(parameters, state, END),
    parseOptionalArchitecturalStyleId(parameters, REVIVAL),
)
