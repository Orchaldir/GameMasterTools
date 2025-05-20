package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.util.font.editFontOption
import at.orchaldir.gm.app.html.model.util.font.parseFontOption
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ONE_M
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

val siPrefix = SiPrefix.Milli

// edit

fun HtmlBlockTag.editTypography(
    state: State,
    typography: Typography,
    hasAuthor: Boolean,
) {
    selectValue("Typography", TYPOGRAPHY, TypographyType.entries, typography.getType()) { type ->
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
    selectValue("Typography Order", combine(TYPOGRAPHY, ORDER), TypographyOrder.entries, typography.order)
    editTypographyLayout(typography.layout, hasAuthor)
}

private fun HtmlBlockTag.editTypographyLayout(layout: TypographyLayout, hasAuthor: Boolean) {
    selectValue("Typography Layout", combine(TYPOGRAPHY, LAYOUT), TypographyLayout.entries, layout) { l ->
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
        )

        when (option) {
            is SimpleStringRenderOption -> {
                editStringSharedOptions(state, param, option.x, option.y, option.font)
            }

            is WrappedStringRenderOption -> {
                editStringSharedOptions(state, param, option.x, option.y, option.font)
                selectDistance("$text Width", combine(param, WIDTH), option.width)
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
    selectDistance("X", combine(param, X), x)
    selectDistance("Y", combine(param, Y), y)
    editFontOption(state, fontOption, combine(param, FONT))
}

private fun HtmlBlockTag.selectDistance(label: String, param: String, value: Distance) {
    selectDistance(label, param, value, ZERO_DISTANCE, ONE_M, siPrefix)
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

private fun parseStringRenderOption(parameters: Parameters, param: String) =
    when (parse(parameters, combine(param, TYPE), StringRenderOptionType.Simple)) {
        StringRenderOptionType.Simple -> SimpleStringRenderOption(
            parseDistance(parameters, combine(param, X), siPrefix, 0),
            parseDistance(parameters, combine(param, Y), siPrefix, 0),
            parseFontOption(parameters, combine(param, FONT)),
        )

        StringRenderOptionType.Wrapped -> WrappedStringRenderOption(
            parseDistance(parameters, combine(param, X), siPrefix, 0),
            parseDistance(parameters, combine(param, Y), siPrefix, 0),
            parseFontOption(parameters, combine(param, FONT)),
            parseDistance(parameters, combine(param, WIDTH), siPrefix, 100),
        )
    }


