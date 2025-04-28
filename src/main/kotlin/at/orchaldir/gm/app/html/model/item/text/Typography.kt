package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.parseFontId
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.*
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// edit

fun HtmlBlockTag.editTypography(
    state: State,
    typography: Typography,
    hasAuthor: Boolean,
) {
    selectValue("Typography", TYPOGRAPHY, TypographyType.entries, typography.getType(), true) { type ->
        when (type) {
            TypographyType.Simple, TypographyType.Advanced -> !hasAuthor
            else -> false
        }
    }

    when (typography) {
        NoTypography -> doNothing()
        is SimpleTitleTypography -> editSimpleTitleTypography(state, typography, hasAuthor)
        is SimpleTypography -> editSimpleTypography(state, typography, hasAuthor)
        is AdvancedTypography -> editAdvancedTypography(state, typography)
    }
}

fun HtmlBlockTag.editSimpleTitleTypography(
    state: State,
    typography: SimpleTitleTypography,
    hasAuthor: Boolean,
) {
    editFontOption(state, "Title", typography.font, TILE)
    editTypographyLayout(typography.layout, hasAuthor)
}

fun HtmlBlockTag.editSimpleTypography(
    state: State,
    typography: SimpleTypography,
    hasAuthor: Boolean,
) {
    editFontOption(state, "Title", typography.title, TILE)
    editFontOption(state, "Author", typography.author, CREATOR)
    selectValue("Typography Order", combine(TYPOGRAPHY, ORDER), TypographyOrder.entries, typography.order, true)
    editTypographyLayout(typography.layout, hasAuthor)
}

private fun HtmlBlockTag.editTypographyLayout(layout: TypographyLayout, hasAuthor: Boolean) {
    selectValue("Typography Layout", combine(TYPOGRAPHY, LAYOUT), TypographyLayout.entries, layout, true) { l ->
        !hasAuthor && l == TypographyLayout.TopAndBottom
    }
}

fun HtmlBlockTag.editAdvancedTypography(
    state: State,
    typography: AdvancedTypography,
) {
    editStringRenderOption(state, typography.author, "Author", CREATOR)
    editStringRenderOption(state, typography.title, "Title", NAME)
}

fun HtmlBlockTag.editStringRenderOption(
    state: State,
    option: StringRenderOption,
    text: String,
    param: String,
) {
    showDetails(text, true) {
        selectValue(
            "Option",
            combine(param, TYPE),
            StringRenderOptionType.entries,
            option.getType(),
            true
        )

        when (option) {
            is SimpleStringRenderOption -> {
                editStringSharedOptions(state, param, option.x, option.y, option.font)
            }

            is WrappedStringRenderOption -> {
                editStringSharedOptions(state, param, option.x, option.y, option.font)
                selectDistance(
                    "$text Width",
                    combine(param, WIDTH),
                    option.width,
                    ZERO,
                    ONE_M,
                    update = true,
                )
            }
        }
    }
}

private fun HtmlBlockTag.editStringSharedOptions(
    state: State,
    param: String,
    x: Distance,
    y: Distance,
    fontOption: FontOption,
) {
    selectDistance("X", combine(param, X), x, ZERO, ONE_M, update = true)
    selectDistance("Y", combine(param, Y), y, ZERO, ONE_M, update = true)
    editFontOption(state, fontOption, combine(param, FONT))
}

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

fun parseTextTypography(parameters: Parameters) = when (parse(parameters, TYPOGRAPHY, TypographyType.None)) {
    TypographyType.None -> NoTypography
    TypographyType.SimpleTitle -> SimpleTitleTypography(
        parseFontOption(parameters, TILE),
        parse(parameters, combine(TYPOGRAPHY, LAYOUT), TypographyLayout.Top),
    )

    TypographyType.Simple -> SimpleTypography(
        parseFontOption(parameters, CREATOR),
        parseFontOption(parameters, TILE),
        parse(parameters, combine(TYPOGRAPHY, ORDER), TypographyOrder.AuthorFirst),
        parse(parameters, combine(TYPOGRAPHY, LAYOUT), TypographyLayout.Top),
    )

    TypographyType.Advanced -> AdvancedTypography(
        parseStringRenderOption(parameters, NAME),
        parseStringRenderOption(parameters, CREATOR),
    )
}

private fun parseFontOption(parameters: Parameters, param: String) =
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

private fun parseStringRenderOption(parameters: Parameters, param: String) =
    when (parse(parameters, combine(param, TYPE), StringRenderOptionType.Simple)) {
        StringRenderOptionType.Simple -> SimpleStringRenderOption(
            parseDistance(parameters, combine(param, X), 0),
            parseDistance(parameters, combine(param, Y), 0),
            parseFontOption(parameters, combine(param, FONT)),
        )

        StringRenderOptionType.Wrapped -> WrappedStringRenderOption(
            parseDistance(parameters, combine(param, X), 0),
            parseDistance(parameters, combine(param, Y), 0),
            parseFontOption(parameters, combine(param, FONT)),
            parseDistance(parameters, combine(param, WIDTH), 100),
        )
    }


