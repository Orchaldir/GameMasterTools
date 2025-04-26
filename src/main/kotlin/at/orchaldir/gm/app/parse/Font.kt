package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseName
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
) = Font(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    state.getFontStorage().getOrThrow(id).base64,
)
