package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.model.fieldFactor
import at.orchaldir.gm.app.html.model.parseFactor
import at.orchaldir.gm.app.html.model.selectPercentage
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseFloat
import at.orchaldir.gm.app.parse.parseUByte
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFill(label: String, fill: Fill) {
    showDetails(label, true) {
        showFill(fill)
    }
}

fun HtmlBlockTag.showFill(fill: Fill) {
    when (fill) {
        is Solid -> field("Solid Fill", fill.color)
        is Transparent -> {
            field("Solid Fill", fill.color)
            fieldFactor("Opacity", fill.opacity)
        }

        is VerticalStripes -> field("Vertical Stripes", "${fill.color0} & ${fill.color1}")
        is HorizontalStripes -> field("Horizontal Stripes", "${fill.color0} & ${fill.color1}")
        is Tiles -> {
            field("Tile Color", fill.fill)
            if (fill.background != null) {
                field("Background Color", fill.background)
            }
        }
    }
}

// edit

fun HtmlBlockTag.selectFill(label: String, fill: Fill, param: String = FILL) {
    showDetails(label, true) {
        selectFill(fill, param)
    }
}

fun HtmlBlockTag.selectFill(fill: Fill, param: String = FILL) {
    selectValue("Fill Type", combine(param, TYPE), FillType.entries, fill.getType(), true)

    when (fill) {
        is Solid -> selectColor(fill.color, selectId = combine(param, COLOR, 0))
        is Transparent -> {
            selectColor(fill.color, selectId = combine(param, COLOR, 0))
            selectPercentage(
                "Opacity",
                combine(param, OPACITY),
                fill.opacity,
                0,
                100,
                1,
                true,
            )
        }

        is VerticalStripes -> selectStripes(fill.color0, fill.color1, fill.width, param)
        is HorizontalStripes -> selectStripes(fill.color0, fill.color1, fill.width, param)
        is Tiles -> {
            val availableTileColors = if (fill.background != null) {
                Color.entries - fill.background
            } else {
                Color.entries
            }
            selectColor(fill.fill, combine(param, COLOR, 0), "Tile Color", availableTileColors)
            selectOptionalColor(
                "Background Color",
                combine(param, COLOR, 1),
                fill.background,
                Color.entries - fill.fill,
                true
            )
            selectFloat("Tile in Meter", fill.width, 0.001f, 100f, 0.01f, combine(param, PATTERN, TILE), true)
            selectPercentage(
                "Border in Percentage",
                combine(param, PATTERN, BORDER),
                fill.borderPercentage,
                1,
                90,
                1,
                true
            )
        }
    }
}

private fun HtmlBlockTag.selectStripes(color0: Color, color1: Color, width: UByte, param: String) {
    selectColor(color0, combine(param, COLOR, 0), "1.Stripe Color", Color.entries - color1)
    selectColor(color1, combine(param, COLOR, 1), "2.Stripe Color", Color.entries - color0)
    selectInt("Stripe Width", width.toInt(), 1, 10, 1, combine(param, PATTERN, WIDTH), true)
}

fun HtmlBlockTag.selectColor(
    color: Color,
    selectId: String,
    label: String = "Color",
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
}

// parse

fun parseFill(parameters: Parameters, param: String = FILL): Fill {
    val type = parse(parameters, combine(param, TYPE), FillType.Solid)

    return when (type) {
        FillType.Solid -> Solid(parse(parameters, combine(param, COLOR, 0), Color.SkyBlue))
        FillType.Transparent -> Transparent(
            parse(parameters, combine(param, COLOR, 0), Color.SkyBlue),
            parseFactor(parameters, combine(param, OPACITY)),
        )

        FillType.VerticalStripes -> VerticalStripes(
            parse(parameters, combine(param, COLOR, 0), Color.Black),
            parse(parameters, combine(param, COLOR, 1), Color.White),
            parseWidth(parameters, param),
        )

        FillType.HorizontalStripes -> HorizontalStripes(
            parse(parameters, combine(param, COLOR, 0), Color.Black),
            parse(parameters, combine(param, COLOR, 1), Color.White),
            parseWidth(parameters, param),
        )

        FillType.Tiles -> Tiles(
            parse(parameters, combine(param, COLOR, 0), Color.Black),
            parse<Color>(parameters, combine(param, COLOR, 1)),
            parseFloat(parameters, combine(param, PATTERN, TILE), 1.0f),
            parseFactor(parameters, combine(param, PATTERN, BORDER), fromPercentage(10))
        )
    }
}

private fun parseWidth(parameters: Parameters, param: String) =
    parseUByte(parameters, combine(param, PATTERN, WIDTH), 1u)