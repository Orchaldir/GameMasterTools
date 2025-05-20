package at.orchaldir.gm.app.html.util.font

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
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

    fieldList(call, state, state.getCurrencyUnits(font.id))
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
    parseSimpleOptionalInt(parameters, param)?.let { FontId(it) }

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
