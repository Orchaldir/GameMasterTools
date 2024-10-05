package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// fill

fun HtmlBlockTag.showFill(fill: Fill) {
    when (fill) {
        is Solid -> field("Solid Fill", fill.color.toString())
        is VerticalStripes -> field("Vertical Stripes", "${fill.color0} & ${fill.color1}")
        is HorizontalStripes -> field("Horizontal Stripes", "${fill.color0} & ${fill.color1}")
        is Tiles -> {
            field("Tile Color", fill.fill.toString())
            if (fill.background != null) {
                field("Background Color", fill.background.toString())
            }
        }
    }
}

fun FORM.selectFill(fill: Fill) {
    selectValue("Fill Type", combine(FILL, TYPE), FillType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = when (fill) {
            is Solid -> type == FillType.Solid
            is VerticalStripes -> type == FillType.VerticalStripes
            is HorizontalStripes -> type == FillType.HorizontalStripes
            is Tiles -> type == FillType.Tiles
        }
    }
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
            val minWidth = (fill.border * 2u + 1u).toInt()
            selectColor(fill.fill, "Tile Color", combine(FILL, COLOR, 0), availableTileColors)
            selectOptionalColor(
                "Background Color",
                combine(FILL, COLOR, 1),
                fill.background,
                Color.entries - fill.fill,
                true
            )
            selectInt("Tile", fill.width.toInt(), minWidth, 1000, combine(PATTERN, TILE), true)
            selectInt("Border", fill.border.toInt(), 1, 1000, combine(PATTERN, BORDER), true)
        }
    }
}

private fun FORM.selectStripes(color0: Color, color1: Color, width: UByte) {
    selectColor(color0, "1.Stripe Color", combine(FILL, COLOR, 0), Color.entries - color1)
    selectColor(color1, "2.Stripe Color", combine(FILL, COLOR, 1), Color.entries - color0)
    selectInt("Stripe Width", width.toInt(), 1, 10, combine(PATTERN, WIDTH), true)
}

fun FORM.selectColor(
    color: Color,
    label: String = "Color",
    selectId: String = combine(FILL, COLOR, 0),
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
}