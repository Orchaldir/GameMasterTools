package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// fill

fun HtmlBlockTag.showFill(fill: Fill) {
    when (fill) {
        is Solid -> field("Solid Fill", fill.color)
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

fun FORM.selectFill(fill: Fill) {
    selectValue("Fill Type", combine(FILL, TYPE), FillType.entries, fill.getType(), true)

    when (fill) {
        is Solid -> selectColor(fill.color)
        is VerticalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
        is HorizontalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
        is Tiles -> {
            val availableTileColors = if (fill.background != null) {
                Color.entries - fill.background
            } else {
                Color.entries
            }
            selectColor(fill.fill, "Tile Color", combine(FILL, COLOR, 0), availableTileColors)
            selectOptionalColor(
                "Background Color",
                combine(FILL, COLOR, 1),
                fill.background,
                Color.entries - fill.fill,
                true
            )
            selectFloat("Tile in Meter", fill.width, 0.001f, 100f, 0.01f, combine(PATTERN, TILE), true)
            selectFloat(
                "Border in Percentage",
                fill.borderPercentage.value,
                0.01f,
                0.9f,
                0.01f,
                combine(PATTERN, BORDER),
                true
            )
        }
    }
}

private fun FORM.selectStripes(color0: Color, color1: Color, width: UByte) {
    selectColor(color0, "1.Stripe Color", combine(FILL, COLOR, 0), Color.entries - color1)
    selectColor(color1, "2.Stripe Color", combine(FILL, COLOR, 1), Color.entries - color0)
    selectInt("Stripe Width", width.toInt(), 1, 10, 1, combine(PATTERN, WIDTH), true)
}

fun FORM.selectColor(
    color: Color,
    label: String = "Color",
    selectId: String = combine(FILL, COLOR, 0),
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
}