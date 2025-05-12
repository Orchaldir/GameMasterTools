package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.font.editFontOption
import at.orchaldir.gm.app.html.model.font.parseFontOption
import at.orchaldir.gm.app.html.model.font.showFontOption
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showContentStyle(
    call: ApplicationCall,
    state: State,
    style: ContentStyle,
) {
    showDetails("Style") {
        showFontOption(call, state, "Main Font", style.main)
        showFontOption(call, state, "Title Font", style.title)
        field("Is Justified?", style.isJustified)
        fieldFactor("Margin", style.margin)
        showInitials(call, state, style.initials)
        field("Min Paragraph Length", style.minParagraphLength)
        field("Max Paragraph Length", style.maxParagraphLength)
    }
}

// edit

fun HtmlBlockTag.editContentStyle(
    state: State,
    style: ContentStyle,
    param: String,
) {
    showDetails("Style", true) {
        editFontOption(state, "Main Font", style.main, combine(param, MAIN))
        editFontOption(state, "Title Font", style.title, combine(param, TITLE))
        selectBool(
            "Is Justified?",
            style.isJustified,
            combine(param, ALIGNMENT),
        )
        selectFactor(
            "Margin",
            combine(param, SIDE),
            style.margin,
            MIN_MARGIN,
            MAX_MARGIN,
            fromPermille(1),
        )
        editInitials(state, style.initials, param)
        selectInt(
            "Min Paragraph Length",
            style.minParagraphLength,
            1,
            1000,
            1,
            combine(param, MIN),
        )
        selectInt(
            "Max Paragraph Length",
            style.maxParagraphLength,
            style.minParagraphLength,
            1000,
            1,
            combine(param, MAX),
        )
    }
}

// parse

fun parseContentStyle(parameters: Parameters, param: String) = ContentStyle(
    parseFontOption(parameters, combine(param, MAIN), DEFAULT_MAIN_SIZE),
    parseFontOption(parameters, combine(param, TITLE), DEFAULT_TITLE_SIZE),
    parseBool(parameters, combine(param, ALIGNMENT)),
    parseFactor(parameters, combine(param, SIDE), DEFAULT_MARGIN),
    parseInitials(parameters, param),
    parseInt(parameters, combine(param, MIN), MIN_PARAGRAPH_LENGTH),
    parseInt(parameters, combine(param, MAX), MAX_PARAGRAPH_LENGTH),
)
