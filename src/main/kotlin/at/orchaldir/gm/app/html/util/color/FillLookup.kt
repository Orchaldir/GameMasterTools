package at.orchaldir.gm.app.html.util.color

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectPercentage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFillLookup(label: String, fill: FillLookup) {
    showDetails(label, true) {
        showFillLookup(fill)
    }
}

fun HtmlBlockTag.showFillLookup(fill: FillLookup) {
    when (fill) {
        is SolidLookup -> fieldColorLookup("Solid Fill", fill.color)
        is TransparentLookup -> {
            fieldColorLookup("Solid Fill", fill.color)
            fieldFactor("Opacity", fill.opacity)
        }

        is VerticalStripesLookup -> field("Vertical Stripes") {
            showColorLookup(fill.color0)
            +" & "
            showColorLookup(fill.color1)
        }

        is HorizontalStripesLookup -> field("Horizontal Stripes") {
            showColorLookup(fill.color0)
            +" & "
            showColorLookup(fill.color1)
        }

        is TilesLookup -> {
            fieldColorLookup("Tile Color", fill.fill)
            fieldColorLookup("Background Color", fill.background)
        }
    }
}

// edit

fun HtmlBlockTag.selectFillLookup(
    state: State,
    label: String,
    fill: FillLookup,
    param: String = FILL,
) {
    showDetails(label, true) {
        selectFillLookup(state, fill, param)
    }
}

fun HtmlBlockTag.selectFillLookup(
    state: State,
    fill: FillLookup,
    param: String = FILL,
) {
    selectValue("Fill Type", combine(param, TYPE), FillLookupType.entries, fill.getType())
    selectFillData(state, fill, param)
}

private fun HtmlBlockTag.selectFillData(
    state: State,
    fill: FillLookup,
    param: String,
) {
    when (fill) {
        is SolidLookup -> editColorLookup(state, "SolidFill", fill.color, combine(param, COLOR, 0), Color.entries)
        is TransparentLookup -> {
            editColorLookup(
                state,
                "Solid Fill",
                fill.color,
                combine(param, COLOR, 0),
                Color.entries,
            )
            selectPercentage(
                "Opacity",
                combine(param, OPACITY),
                fill.opacity,
                0,
                100,
                1,
            )
        }

        is VerticalStripesLookup -> selectStripes(state, fill.color0, fill.color1, fill.width, param)
        is HorizontalStripesLookup -> selectStripes(state, fill.color0, fill.color1, fill.width, param)
        is TilesLookup -> {
            selectTwoColors(
                state,
                param,
                fill.fill,
                fill.background,
                "Tile Color",
                "Background Color",
            )
            selectFloat("Tile in Meter", fill.width, 0.001f, 100f, 0.01f, combine(param, PATTERN, TILE))
            selectPercentage(
                "Border in Percentage",
                combine(param, PATTERN, BORDER),
                fill.borderPercentage,
                1,
                90,
                1,
            )
        }
    }
}

private fun HtmlBlockTag.selectStripes(
    state: State,
    lookup0: ColorLookup,
    lookup1: ColorLookup,
    width: Distance,
    param: String,
) {
    selectTwoColors(state, param, lookup0, lookup1, "1.Stripe Color", "2.Stripe Color")
    selectStripeWidth(param, width)
}

private fun HtmlBlockTag.selectTwoColors(
    state: State,
    param: String,
    lookup0: ColorLookup,
    lookup1: ColorLookup,
    label0: String,
    label1: String,
) {
    editColorLookup(
        state,
        label0,
        lookup0,
        combine(param, COLOR, 0),
        lookup1.getOtherColors(),
    )
    editColorLookup(
        state,
        label1,
        lookup1,
        combine(param, COLOR, 1),
        lookup0.getOtherColors(),
    )
}

// parse

fun parseOptionalFillLookup(parameters: Parameters, param: String = FILL): FillLookup? {
    val type = parse<FillLookupType>(parameters, combine(param, TYPE))

    return if (type != null) {
        parseFillLookupOfType(parameters, param, type)
    } else {
        null
    }
}

fun parseFillLookup(parameters: Parameters, param: String = FILL): FillLookup {
    val type = parse(parameters, combine(param, TYPE), FillLookupType.Solid)

    return parseFillLookupOfType(parameters, param, type)
}

private fun parseFillLookupOfType(
    parameters: Parameters,
    param: String,
    type: FillLookupType,
) = when (type) {
    FillLookupType.Solid -> SolidLookup(
        parseColorLookup(parameters, combine(param, COLOR, 0), Color.SkyBlue),
    )

    FillLookupType.Transparent -> TransparentLookup(
        parseColorLookup(parameters, combine(param, COLOR, 0), Color.SkyBlue),
        parseFactor(parameters, combine(param, OPACITY)),
    )

    FillLookupType.VerticalStripes -> VerticalStripesLookup(
        parseColorLookup(parameters, combine(param, COLOR, 0), Color.Black),
        parseColorLookup(parameters, combine(param, COLOR, 1), Color.White),
        parseStripeWidth(parameters, param),
    )

    FillLookupType.HorizontalStripes -> HorizontalStripesLookup(
        parseColorLookup(parameters, combine(param, COLOR, 0), Color.Black),
        parseColorLookup(parameters, combine(param, COLOR, 1), Color.White),
        parseStripeWidth(parameters, param),
    )

    FillLookupType.Tiles -> TilesLookup(
        parseColorLookup(parameters, combine(param, COLOR, 0), Color.Black),
        parseColorLookup(parameters, combine(param, COLOR, 1), Color.White),
        parseFloat(parameters, combine(param, PATTERN, TILE), 1.0f),
        parseFactor(parameters, combine(param, PATTERN, BORDER), fromPercentage(10))
    )
}
