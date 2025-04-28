package at.orchaldir.gm.app.html.model.font

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.item.getTexts
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showFont(
    call: ApplicationCall,
    state: State,
    font: Font,
) {
    optionalField(call, state, "Date", font.date)
    field("Base64") {
        textArea("10", "200", TextAreaWrap.soft) {
            +font.base64
        }
    }
    h2 { +"Usage" }

    fieldList(call, state, state.getTexts(font.id))
}

// edit

fun FORM.editFont(
    state: State,
    font: Font,
) {
    selectName(font.name)
    selectOptionalDate(state, "Date", font.date, DATE)
}

// parse

fun parseFontId(
    parameters: Parameters,
    param: String,
) = FontId(parseInt(parameters, param))

fun parseOptionalFontId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { FontId(it) }

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
