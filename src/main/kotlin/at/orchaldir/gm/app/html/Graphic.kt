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
        }
    }
    when (fill) {
        is Solid -> selectColor(fill.color)
        is VerticalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
        is HorizontalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
    }
}

private fun FORM.selectStripes(color0: Color, color1: Color, width: UByte) {
    selectColor(color0, "1.Stripe Color", combine(FILL, COLOR, 0), Color.entries - color1)
    selectColor(color1, "2.Stripe Color", combine(FILL, COLOR, 0), Color.entries - color0)
    selectInt("Stripe Width", width.toInt(), 1, 10, PATTERN_WIDTH, true)
}

fun FORM.selectColor(
    color: Color,
    label: String = "Color",
    selectId: String = combine(FILL, COLOR, 0),
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
}