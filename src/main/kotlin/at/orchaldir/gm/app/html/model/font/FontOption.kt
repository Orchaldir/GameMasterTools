package at.orchaldir.gm.app.html.model.font

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

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
    selectValue("Font Option", combine(param, TYPE), FontOptionType.entries, option.getType(), true)

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
        HUNDRED_µM,
        update = true,
    )
}

private fun HtmlBlockTag.editSharedFontOptions(
    state: State,
    param: String,
    fontId: FontId,
    size: Distance,
) {
    selectElement(
        state,
        "Font",
        combine(param, FONT),
        state.getFontStorage().getAll(),
        fontId,
        true,
    )
    selectDistance(
        "Font Size",
        combine(param, SIZE),
        size,
        ONE_MM,
        ONE_M,
        update = true
    )
}

// parse

fun parseFontOption(parameters: Parameters, param: String) =
    when (parse(parameters, combine(param, TYPE), FontOptionType.Solid)) {
        FontOptionType.Solid -> SolidFont(
            parseDistance(parameters, combine(param, SIZE), 10),
            parse(parameters, combine(param, COLOR), Color.White),
            parseFontId(parameters, combine(param, FONT)),
        )

        FontOptionType.Border -> FontWithBorder(
            parseDistance(parameters, combine(param, SIZE), 10),
            parseDistance(parameters, combine(param, BORDER, SIZE), 1),
            parse(parameters, combine(param, COLOR), Color.White),
            parse(parameters, combine(param, BORDER, COLOR), Color.White),
            parseFontId(parameters, combine(param, FONT)),
        )

        FontOptionType.Hollow -> HollowFont(
            parseDistance(parameters, combine(param, SIZE), 10),
            parseDistance(parameters, combine(param, BORDER, SIZE), 1),
            parse(parameters, combine(param, BORDER, COLOR), Color.White),
            parseFontId(parameters, combine(param, FONT)),
        )
    }
