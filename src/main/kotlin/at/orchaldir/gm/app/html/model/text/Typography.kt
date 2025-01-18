package at.orchaldir.gm.app.html.model.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.selectDistance
import at.orchaldir.gm.app.html.selectColor
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing

import at.orchaldir.gm.utils.math.Distance
import kotlinx.html.HtmlBlockTag

private val ZERO_MM = Distance(0)
private val ONE_MM = Distance(1)
private val THOUSAND_MM = Distance(1000)

// edit

fun HtmlBlockTag.editTypography(typography: Typography) {
    selectValue("Typography", TYPOGRAPHY, TypographyType.entries, typography.getType(), true)

    when (typography) {
        NoTypography -> doNothing()
        is SimpleTitleTypography -> editSimpleTitleTypography(typography)
        is SimpleTypography -> editSimpleTypography(typography)
        is AdvancedTypography -> editAdvancedTypography(typography)
    }
}

fun HtmlBlockTag.editSimpleTitleTypography(
    typography: SimpleTitleTypography,
) {
    editFontOption(typography.font, "Title", NAME)
    selectValue("Typography Layout", combine(TYPOGRAPHY, LAYOUT), TypographyLayout.entries, typography.layout, true)
}

fun HtmlBlockTag.editSimpleTypography(
    typography: SimpleTypography,
) {
    editFontOption(typography.title, "Title", NAME)
    editFontOption(typography.author, "Title", CREATOR)
    selectValue("Typography Order", combine(TYPOGRAPHY, ORDER), TypographyOrder.entries, typography.order, true)
    selectValue("Typography Layout", combine(TYPOGRAPHY, LAYOUT), TypographyLayout.entries, typography.layout, true)
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
    selectValue(
        "$text String Option Type",
        combine(param, TYPE),
        StringRenderOptionType.entries,
        option.getType(),
        true
    )

    when (option) {
        is SimpleStringRenderOption -> {
            editStringSharedOptions(text, param, option.x, option.y, option.fontOption)
        }

        is WrappedStringRenderOption -> {
            editStringSharedOptions(text, param, option.x, option.y, option.fontOption)
            selectDistance("$text Width", combine(param, WIDTH), option.width, ZERO_MM, THOUSAND_MM, update = true)
        }
    }
}

private fun HtmlBlockTag.editStringSharedOptions(
    text: String,
    param: String,
    x: Distance,
    y: Distance,
    fontOption: FontOption,
) {
    selectDistance("$text X", combine(param, X), x, ZERO_MM, THOUSAND_MM, update = true)
    selectDistance("$text Y", combine(param, Y), y, ZERO_MM, THOUSAND_MM, update = true)
    editFontOption(fontOption, "$text Font", combine(param, FONT))
}

fun HtmlBlockTag.editFontOption(
    option: FontOption,
    text: String,
    param: String,
) {
    selectValue("$text Font Option Type", combine(param, FONT), FontOptionType.entries, option.getType(), true)

    when (option) {
        is SolidFont -> {
            selectColor("$text Font Color", combine(param, COLOR), Color.entries, option.color)
            selectDistance("$text Font Size", combine(param, SIZE), option.size, ONE_MM, THOUSAND_MM, update = true)
        }

        is FontWithBorder -> {
            selectColor("$text Fill Color", combine(param, COLOR), Color.entries, option.fill)
            selectColor("$text Border Color", combine(param, BORDER, COLOR), Color.entries, option.border)
            selectDistance("$text Font Size", combine(param, SIZE), option.size, ONE_MM, THOUSAND_MM, update = true)
            selectDistance(
                "$text Border Thickness", combine(param, BORDER, SIZE), option.thickness,
                ONE_MM, Distance(100), update = true
            )
        }
    }
}




