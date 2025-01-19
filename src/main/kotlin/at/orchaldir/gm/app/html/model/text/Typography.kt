package at.orchaldir.gm.app.html.model.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.parseDistance
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.item.text.book.FontOption
import at.orchaldir.gm.core.model.item.text.book.FontOptionType
import at.orchaldir.gm.core.model.item.text.book.FontWithBorder
import at.orchaldir.gm.core.model.item.text.book.SolidFont
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

private val ZERO_MM = Distance(0)
private val ONE_MM = Distance(1)
private val THOUSAND_MM = Distance(1000)

// edit

fun HtmlBlockTag.editTypography(typography: Typography, hasAuthor: Boolean) {
    selectValue("Typography", TYPOGRAPHY, TypographyType.entries, typography.getType(), true) { type ->
        when (type) {
            TypographyType.Simple, TypographyType.Advanced -> !hasAuthor
            else -> false
        }
    }

    when (typography) {
        NoTypography -> doNothing()
        is SimpleTitleTypography -> editSimpleTitleTypography(typography, hasAuthor)
        is SimpleTypography -> editSimpleTypography(typography, hasAuthor)
        is AdvancedTypography -> editAdvancedTypography(typography)
    }
}

fun HtmlBlockTag.editSimpleTitleTypography(
    typography: SimpleTitleTypography,
    hasAuthor: Boolean,
) {
    editFontOption("Title", typography.font, NAME)
    editTypographyLayout(typography.layout, hasAuthor)
}

fun HtmlBlockTag.editSimpleTypography(
    typography: SimpleTypography,
    hasAuthor: Boolean,
) {
    editFontOption("Title", typography.title, NAME)
    editFontOption("Author", typography.author, CREATOR)
    selectValue("Typography Order", combine(TYPOGRAPHY, ORDER), TypographyOrder.entries, typography.order, true)
    editTypographyLayout(typography.layout, hasAuthor)
}

private fun HtmlBlockTag.editTypographyLayout(layout: TypographyLayout, hasAuthor: Boolean) {
    selectValue("Typography Layout", combine(TYPOGRAPHY, LAYOUT), TypographyLayout.entries, layout, true) { l ->
        !hasAuthor && l == TypographyLayout.TopAndBottom
    }
}

fun HtmlBlockTag.editAdvancedTypography(
    typography: AdvancedTypography,
) {
    editStringRenderOption(typography.author, "Author", CREATOR)
    editStringRenderOption(typography.title, "Title", NAME)
}

fun HtmlBlockTag.editStringRenderOption(
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
                editStringSharedOptions(param, option.x, option.y, option.fontOption)
            }

            is WrappedStringRenderOption -> {
                editStringSharedOptions(param, option.x, option.y, option.fontOption)
                selectDistance("$text Width", combine(param, WIDTH), option.width, ZERO_MM, THOUSAND_MM, update = true)
            }
        }
    }
}

private fun HtmlBlockTag.editStringSharedOptions(
    param: String,
    x: Distance,
    y: Distance,
    fontOption: FontOption,
) {
    selectDistance("X", combine(param, X), x, ZERO_MM, THOUSAND_MM, update = true)
    selectDistance("Y", combine(param, Y), y, ZERO_MM, THOUSAND_MM, update = true)
    editFontOption(fontOption, combine(param, FONT))
}

fun HtmlBlockTag.editFontOption(
    text: String,
    option: FontOption,
    param: String,
) {
    showDetails(text, true) {
        editFontOption(option, param)
    }
}

fun HtmlBlockTag.editFontOption(
    option: FontOption,
    param: String,
) {
    selectValue("Font Option", combine(param, FONT), FontOptionType.entries, option.getType(), true)

    when (option) {
        is SolidFont -> {
            selectColor("Font Color", combine(param, COLOR), Color.entries, option.color)
            selectDistance("Font Size", combine(param, SIZE), option.size, ONE_MM, THOUSAND_MM, update = true)
        }

        is FontWithBorder -> {
            selectColor("Fill Color", combine(param, COLOR), Color.entries, option.fill)
            selectColor("Border Color", combine(param, BORDER, COLOR), Color.entries, option.border)
            selectDistance("Font Size", combine(param, SIZE), option.size, ONE_MM, THOUSAND_MM, update = true)
            selectDistance(
                "Border Thickness",
                combine(param, BORDER, SIZE),
                option.thickness,
                ONE_MM,
                Distance(100),
                update = true,
            )
        }
    }
}

// parse

fun parseTextTypography(parameters: Parameters) = when (parse(parameters, TYPOGRAPHY, TypographyType.None)) {
    TypographyType.None -> NoTypography
    TypographyType.SimpleTitle -> SimpleTitleTypography(
        parseFontOption(parameters, NAME),
        parse(parameters, combine(TYPOGRAPHY, LAYOUT), TypographyLayout.Top),
    )

    TypographyType.Simple -> SimpleTypography(
        parseFontOption(parameters, CREATOR),
        parseFontOption(parameters, NAME),
        parse(parameters, combine(TYPOGRAPHY, ORDER), TypographyOrder.AuthorFirst),
        parse(parameters, combine(TYPOGRAPHY, LAYOUT), TypographyLayout.Top),
    )

    TypographyType.Advanced -> AdvancedTypography(
        parseStringRenderOption(parameters, NAME),
        parseStringRenderOption(parameters, CREATOR),
    )
}

private fun parseFontOption(parameters: Parameters, param: String) =
    when (parse(parameters, combine(param, FONT), FontOptionType.Solid)) {
        FontOptionType.Solid -> SolidFont(
            parse(parameters, combine(param, COLOR), Color.White),
            parseDistance(parameters, combine(param, SIZE), 10),
        )

        FontOptionType.Border -> FontWithBorder(
            parse(parameters, combine(param, COLOR), Color.White),
            parse(parameters, combine(param, BORDER, COLOR), Color.White),
            parseDistance(parameters, combine(param, SIZE), 10),
            parseDistance(parameters, combine(param, BORDER, SIZE), 1),
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


