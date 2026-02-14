package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOptionalFill(label: String, fill: Fill?) {
    if (fill != null) {
        showDetails(label, true) {
            showFill(fill)
        }
    }
}

fun HtmlBlockTag.showOptionalFill(fill: Fill?) {
    if (fill != null) {
        showFill(fill)
    }
}

fun HtmlBlockTag.showFill(label: String, fill: Fill) {
    showDetails(label, true) {
        showFill(fill)
    }
}

fun HtmlBlockTag.showFill(fill: Fill) {
    when (fill) {
        is Circles -> {
            fieldColor(fill.circle, "Circle Color")
            fieldOptionalColor(fill.background, "Background Color")
        }
        is Solid -> fieldColor(fill.color, "Solid Fill")
        is Transparent -> {
            fieldColor(fill.color, "Solid Fill")
            fieldFactor("Opacity", fill.opacity)
        }

        is VerticalStripes -> field("Vertical Stripes") {
            showOptionalColor(fill.color0)
            +" & "
            showOptionalColor(fill.color1)
        }

        is HorizontalStripes -> field("Horizontal Stripes") {
            showOptionalColor(fill.color0)
            +" & "
            showOptionalColor(fill.color1)
        }

        is Tiles -> {
            fieldColor(fill.fill, "Tile Color")
            fieldOptionalColor(fill.background, "Background Color")
        }
    }
}

// edit

fun HtmlBlockTag.selectOptionalFill(label: String, fill: Fill?, param: String = FILL) {
    showDetails(label, true) {
        selectOptionalFill(fill, param)
    }
}

fun HtmlBlockTag.selectFill(label: String, fill: Fill, param: String = FILL) {
    showDetails(label, true) {
        selectFill(fill, param)
    }
}

fun HtmlBlockTag.selectOptionalFill(fill: Fill?, param: String = FILL) {
    selectOptionalValue("Fill Type", combine(param, TYPE), fill?.getType(), FillType.entries)

    if (fill != null) {
        selectFillData(fill, param)
    }
}

fun HtmlBlockTag.selectFill(fill: Fill, param: String = FILL) {
    selectValue("Fill Type", combine(param, TYPE), FillType.entries, fill.getType())
    selectFillData(fill, param)
}

private fun HtmlBlockTag.selectFillData(
    fill: Fill,
    param: String,
) {
    when (fill) {
        is Circles -> {
            selectFillAndBackgroundColors(
                fill.circle,
                fill.background,
                param,
                "Circle Color",
            )
            selectWidth(combine(param, PATTERN, TILE), fill.width)
            selectPercentage(
                "Radius",
                combine(param, PATTERN, RADIUS),
                fill.radiusPercentage,
                1,
                90,
                1,
            )
        }
        is Solid -> selectColor(fill.color, combine(param, COLOR, 0))
        is Transparent -> {
            selectColor(fill.color, combine(param, COLOR, 0))
            selectPercentage(
                "Opacity",
                combine(param, OPACITY),
                fill.opacity,
                0,
                100,
                1,
            )
        }

        is VerticalStripes -> selectStripes(fill.color0, fill.color1, fill.width, param)
        is HorizontalStripes -> selectStripes(fill.color0, fill.color1, fill.width, param)
        is Tiles -> {
            selectFillAndBackgroundColors(
                fill.fill,
                fill.background,
                param,
                "Tile Color",
            )
            selectWidth(combine(param, PATTERN, TILE), fill.width)
            selectPercentage(
                "Border",
                combine(param, PATTERN, BORDER),
                fill.borderPercentage,
                1,
                90,
                1,
            )
        }
    }
}

private fun HtmlBlockTag.selectFillAndBackgroundColors(
    shape: Color,
    background: Color?,
    param: String,
    shapeLabel: String,
) {
    val availableTileColors = if (background != null) {
        Color.entries - background
    } else {
        Color.entries
    }
    selectColor(shape, combine(param, COLOR, 0), shapeLabel, availableTileColors)
    selectOptionalColor(
        background,
        combine(param, COLOR, 1),
        "Background Color",
        Color.entries - shape,
    )
}

fun HtmlBlockTag.selectStripes(color0: Color, color1: Color, width: Distance, param: String) {
    selectColor(color0, combine(param, COLOR, 0), "1.Stripe Color", Color.entries - color1)
    selectColor(color1, combine(param, COLOR, 1), "2.Stripe Color", Color.entries - color0)
    selectStripeWidth(param, width)
}

fun HtmlBlockTag.selectStripeWidth(param: String, width: Distance) = selectWidth(
    param,
    width,
    "Stripe Width",
)

fun HtmlBlockTag.selectWidth(param: String, width: Distance, label: String = "Width") = selectDistance(
    label,
    combine(param, PATTERN, WIDTH),
    width,
    1,
    100,
    SiPrefix.Centi,
)

// parse

fun parseOptionalFill(parameters: Parameters, param: String = FILL): Fill? {
    val type = parse<FillType>(parameters, combine(param, TYPE))

    return if (type != null) {
        parseFillOfType(parameters, param, type)
    } else {
        null
    }
}

fun parseFill(parameters: Parameters, param: String = FILL): Fill {
    val type = parse(parameters, combine(param, TYPE), FillType.Solid)

    return parseFillOfType(parameters, param, type)
}

private fun parseFillOfType(
    parameters: Parameters,
    param: String,
    type: FillType,
) = when (type) {
    FillType.Circles -> Circles(
        parse(parameters, combine(param, COLOR, 0), Color.Black),
        parse<Color>(parameters, combine(param, COLOR, 1)),
        parseWidth(parameters, param),
        parseFactor(parameters, combine(param, PATTERN, RADIUS), HALF)
    )
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
        parseWidth(parameters, param),
        parseFactor(parameters, combine(param, PATTERN, BORDER), fromPercentage(10))
    )
}

fun parseWidth(parameters: Parameters, param: String) = parseDistance(
    parameters,
    combine(param, PATTERN, WIDTH),
    SiPrefix.Centi,
    10,
)