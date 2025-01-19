package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.font.FontId
import io.ktor.http.*

fun parseFontId(
    parameters: Parameters,
    param: String,
) = FontId(parseInt(parameters, param))
