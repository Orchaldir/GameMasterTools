package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFontId(
    parameters: Parameters,
    param: String,
) = FontId(parseInt(parameters, param))

fun parseFont(id: FontId, parameters: Parameters) = Font(
    id,
    parameters.getOrFail(NAME),
    parseString(parameters, CONTENT),
)

