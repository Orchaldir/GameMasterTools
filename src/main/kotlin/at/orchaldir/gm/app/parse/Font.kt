package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFontId(
    parameters: Parameters,
    param: String,
) = FontId(parseInt(parameters, param))

fun parseFont(
    parameters: Parameters,
    state: State,
    id: FontId,
    base64: String,
) = Font(
    id,
    parameters.getOrFail(NAME),
    parseOptionalDate(parameters, state, DATE),
    base64
)
