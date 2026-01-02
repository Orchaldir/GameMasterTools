package at.orchaldir.gm.app.html.util.font

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldDistance
import at.orchaldir.gm.app.html.util.math.parseDistance
import at.orchaldir.gm.app.html.util.math.selectDistance
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.*
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFontOption(
    call: ApplicationCall,
    state: State,
    text: String,
    options: FontOption,
) {
    showDetails(text) {
        showFontOption(call, state, options)
    }
}

fun HtmlBlockTag.showFontOption(
    call: ApplicationCall,
    state: State,
    option: FontOption,
) {
    field("Font Option", option.getType())

    when (option) {
        is SolidFont -> {
            fieldColor(option.color, "Font Color")
            showSharedFontOptions(call, state, option.font, option.size)
        }

        is FontWithBorder -> {
            fieldColor(option.fill, "Fill Color")
            fieldColor(option.border, "Border Color")
            showSharedFontOptions(call, state, option.font, option.size)
            fieldDistance("Border Thickness", option.thickness)
        }

        is HollowFont -> {
            fieldColor(option.border, "Border Color")
            showSharedFontOptions(call, state, option.font, option.size)
            fieldDistance("Border Thickness", option.thickness)
        }
    }
}

private fun HtmlBlockTag.showSharedFontOptions(
    call: ApplicationCall,
    state: State,
    fontId: FontId?,
    size: Distance,
) {
    optionalFieldLink("Font", call, state, fontId)
    fieldDistance("Font Size", size)
}

// edit

fun HtmlBlockTag.editFontOption(
    state: State,
    text: String,
    option: FontOption,
    param: String,
) {
    showDetails(text, true) {
        editFontOption(state, option, param)
    }
}

fun HtmlBlockTag.editFontOption(
    state: State,
    option: FontOption,
    param: String,
) {
    selectValue("Font Option", combine(param, TYPE), FontOptionType.entries, option.getType())

    when (option) {
        is SolidFont -> {
            selectColor(option.color, combine(param, COLOR), "Font Color")
            editSharedFontOptions(state, param, option.font, option.size)
        }

        is FontWithBorder -> {
            selectColor(option.fill, combine(param, COLOR), "Fill Color")
            selectColor(option.border, combine(param, BORDER, COLOR), "Border Color")
            editSharedFontOptions(state, param, option.font, option.size)
            selectBorderThickness(param, option.thickness)
        }

        is HollowFont -> {
            selectColor(option.border, combine(param, BORDER, COLOR), "Border Color")
            editSharedFontOptions(state, param, option.font, option.size)
            selectBorderThickness(param, option.thickness)
        }
    }
}

private fun HtmlBlockTag.selectBorderThickness(
    param: String,
    thickness: Distance,
) {
    selectDistance(
        "Border Thickness",
        combine(param, BORDER, SIZE),
        thickness,
        HUNDRED_µM,
        ONE_CM,
        SiPrefix.Micro,
    )
}

private fun HtmlBlockTag.editSharedFontOptions(
    state: State,
    param: String,
    fontId: FontId?,
    size: Distance,
) {
    selectOptionalElement(
        state,
        "Font",
        combine(param, FONT),
        state.getFontStorage().getAll(),
        fontId,
    )
    selectDistance(
        "Font Size",
        combine(param, SIZE),
        size,
        ONE_MM,
        ONE_M,
        SiPrefix.Milli,
    )
}

// parse

fun parseFontOption(parameters: Parameters, param: String, defaultSize: Distance = fromMillimeters(10)) =
    when (parse(parameters, combine(param, TYPE), FontOptionType.Solid)) {
        FontOptionType.Solid -> SolidFont(
            parseFontSize(parameters, param, defaultSize),
            parseFontColor(parameters, param),
            parseOptionalFontId(parameters, combine(param, FONT)),
        )

        FontOptionType.Border -> FontWithBorder(
            parseFontSize(parameters, param, defaultSize),
            parseBorderThickness(parameters, param),
            parseFontColor(parameters, param),
            parseBorderColor(parameters, param),
            parseOptionalFontId(parameters, combine(param, FONT)),
        )

        FontOptionType.Hollow -> HollowFont(
            parseFontSize(parameters, param, defaultSize),
            parseBorderThickness(parameters, param),
            parseBorderColor(parameters, param),
            parseOptionalFontId(parameters, combine(param, FONT)),
        )
    }

private fun parseBorderThickness(parameters: Parameters, param: String) =
    parseDistance(parameters, combine(param, BORDER, SIZE), SiPrefix.Micro, 1)

private fun parseFontSize(parameters: Parameters, param: String, defaultSize: Distance) =
    parseDistance(parameters, combine(param, SIZE), SiPrefix.Milli, defaultSize)

private fun parseFontColor(parameters: Parameters, param: String) =
    parse(parameters, combine(param, COLOR), Color.Black)

private fun parseBorderColor(parameters: Parameters, param: String) =
    parse(parameters, combine(param, BORDER, COLOR), Color.Blue)
